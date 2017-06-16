# Spring AOP的注解使用方式
05 Spring Aop实例（AOP 如此简单）@Aspect、@Around 注解方式配置 - 简书
http://www.jianshu.com/p/9517c90db0d4

annotation_param_pro分支实现了
能够在切面中传递参数
参数来源于注解接口定义
在切面中传递参数后，能够在业务类中使用此参数


version1.0
Spring中的AOP（五）——在Advice方法中获取目标方法的参数 - 摆渡者
	https://my.oschina.net/itblog/blog/211693
	Spring AOP切面实现：参数传递 - LittleSkey的博客 - 博客频道 - CSDN.NET
	http://blog.csdn.net/LittleSkey/article/details/51842917
	Spring AOP 注解方式实现的一些“坑” - 简书
	http://www.jianshu.com/p/def4c497571c
	
	execution中写到接口（IHello.sayhello）就可以
	参数就是接口里的参数内容
	
!!!version2.0
	现在假设IHello.sayhello是一个通用业务类型接口，每一个业务根据不同类型都要进行一个不同的实现，
	比如实现类分为学生类和老师类，学生的hello与老师的hello内容不同，根据两者的hello业务进行不同的切面处理(如老师的hello前切面要加：老师开始讲话。学生也是)
	如果要实现此需求，就不能统一这样写了，而在切面里写if else去判断老师和学生是不能扩展的，万一还有院长，以及校长讲话呢。。。
	一、现在要做的是对aspect进行封装成接口，使得想写谁的（学生/老师）切面，就实现一个切面接口（IAspect）或者切面抽象类，
	二、然后切面接口IAspect里面定义了切面的所有方法（before/after等），
	三、然后在实现切面接口的类里，把业务类型传进去
	（类型的值一般是老师表或者student模型或表名）
	[类型的来源是从方法的参数里来的，如从hello里来的，然后路由到AspectHandler里再找到具体切面]
	[传递方法通过定义注解IParm,然后在切面实现类通过@IParam(args="teacher")]
	四、然后写相关的具体before或after内容
	
	见图片：本目录》spring.aop.png
	
	五、做完这一切后，我们有了IAspect>AspectTeacher(@Iparam)
	IHello>TeacherHello/StudentHello
	还需要什么？
	需要能够在调用IHello实现的时候，能够自动路由走Iaspect
	
	Spring AOP中定义切点（PointCut）和通知（Advice）-aop,spring 相关文章-天码营
https://www.tianmaying.com/tutorial/spring-aop-point-advice



不生效原因集锦：
1、配置execution出错
在	@Before("execution(* com.paymoon.basic.spring.aop.demo.service.IHello.sayHello(..))")
中，少了一个后面的括号，错的是sayHello(..)")，正确是sayHello(..))")
2、xml 
base-package="com.paymoon.basic.spring.aop.demo.service">，可以找到Ihello的实现，但是找不到aspect
base-package="com.paymoon.basic.spring.aop.demo">，才可以找到aspect,但是Error creating bean with name 'aspectTeacher'
正确的方式配置多个


3、0 can't find referenced pointcut beforeHelloAspect
before语法不对
4、注意pointcut与before联合使用后，相关的before语法已经变样
Spring 之AOP AspectJ切入点语法详解（最全了，不需要再去其他地找了） - Java - 学习交流 - 私塾在线 - 只做精品视频课程服务
http://sishuok.com/forum/posts/list/281.html

	@Pointcut("execution(* com.paymoon.basic.spring.aop.demo.service.IHello.sayHello(..))")
	    @Before("beforeHelloAspect()")
	而
	    @Around("execution(* beforeHelloAspect(..))")
	则是错的
	
	
5、跑通日志！
	
	10:08:12.267 INFO  com.paymoon.basic.spring.aop.demo.aspect.AspectHandler.aroundHelloAspectHandler()/45  - aroundHelloAspectHandler..start!!!!!!!!
 com/paymoon/basic/spring/aop/demo/aspect/AspectHandler beforeHelloAspectHandler..start!!!!!!!!
already find aspect!com.paymoon.basic.spring.aop.demo.aspect.impl.AspectTeacher@41382722
AspectTeacher,beforeHello,type:teacher
I'm teacher, I can say hello to students, here is helloParam: >teacher
AspectTeacher,afterHello,type:teacher

从日志上看，得知around是在before之前的


6、查看切面
 IHello userService =  context.getBean("helloTeacher",IHello.class);
        userService.sayHello("teacher");
        userService =  context.getBean("helloStudent",IHello.class);
        userService.sayHello("student");
        context.close();
        
 日志
       ````
       	17-05-2017 11:38:16,962 CST INFO  [main]     com.paymoon.basic.spring.aop.demo.aspect.AspectHandler@44] aroundHelloAspectHandler..start!!!!!!!!
    	beforeHelloAspectHandler..start!!!!!!!!
		already find aspect!	com.paymoon.basic.spring.aop.demo.aspect.impl.AspectTeacher@6c451c9c
		AspectTeacher,beforeHello,type:teacher
		I'm teacher, I can say hello to students, here is helloParam: >teacher
		AspectTeacher,afterHello,type:teacher
		17-05-2017 11:38:17,381 CST INFO  [main] com.paymoon.basic.spring.aop.demo.aspect.AspectHandler@44] aroundHelloAspectHandler..start!!!!!!!!
		beforeHelloAspectHandler..start!!!!!!!!
		hello,I'm students,  here is helloParam: >student
		````
        可知在student的时候，是没有用到切面的
        
 参考 
 Spring中的AOP（五）——在Advice方法中获取目标方法的参数 - 摆渡者
https://my.oschina.net/itblog/blog/211693
Spring AOP切面实现：参数传递 - LittleSkey的博客 - 博客频道 - CSDN.NET
http://blog.csdn.net/LittleSkey/article/details/51842917
Spring 中使用 AOP 切面解析处理自定义注解 - 后端 - 掘金
https://juejin.im/entry/5849175db123db0066048508
