package com.paymoon.basic.spring.aop.demo.aspect;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.paymoon.basic.commons.exception.BaseException;
/**
 * @author yol
 *
 */
@Service
@Aspect
public class AspectHandler {
	private static Logger logger = LogManager.getLogger();
	@Pointcut("execution(* com.paymoon.basic.spring.aop.demo.service.IHello.sayHello(..))")
	public void beforeHelloAspect() {
	}
	
//    @Before("execution(* com.paymoon.basic.spring.aop.demo.aspect.AspectHandler.beforeHelloAspect(..))")
    @Before("beforeHelloAspect()")
	public void beforeHelloAspectHandler(JoinPoint joinPoint) throws BaseException {
    	System.out.println("beforeHelloAspectHandler..start!!!!!!!!");
		Object[] args = joinPoint.getArgs();
		String type = (String) args[0];//传递类型teacher or student
		IAspect aspect = getImplementClazz(type);
		if (aspect != null) {
			System.out.println("already find aspect!"+aspect);
			aspect.beforeHello(type, null);
		}
	}
    @Around("beforeHelloAspect()")
	public Map<String, Object> aroundHelloAspectHandler(ProceedingJoinPoint joinPoint) throws Throwable      {
    	logger.info("aroundHelloAspectHandler..start!!!!!!!!");
		Object[] args = joinPoint.getArgs();
		String type = (String) args[0];
		IAspect aspect = getImplementClazz(type);
		Map<String, Object> result = (Map<String, Object >) joinPoint.proceed(args);
		if (aspect != null) {
			aspect.afterHello(type, null,result);
		}
		return result;
	}
    
    /**
     * 获取切面具体的实现
     * @param tableName
     * @return
     */
    private IAspect getImplementClazz(final String argsFormHello) {
		int findMax = 10;
		List<Class> clazzs = ClassFinder.getAllClassByInterfaceViaSpring(IAspect.class);
		IAspect aspect = null;
		try {
				for (int i = 0; i < clazzs.size(); i++) {
					Class c = clazzs.get(i);
					IParam param = (IParam) c.getAnnotation(IParam.class);//从这里读取每个实现切面类时，注解上的业务变量参数（teacher/student）
					if (param==null) {//支持切面抽象类，如果没有变量，则略过
						continue;
					}
					String argsFormIParam = param.args();
					if (argsFormIParam.equals(argsFormHello)) {//如果切面实现中的变量与hello方法中参数变量相同，则返回具体的切面实现
						ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("root-context.xml");

				        //IHello userService = context.getBean(IHello.class);
						aspect = (IAspect)context.getBean(c);
						//TODO 可以用一个app utils
						//Spring在代码中获取bean的几种方式
						//http://shaofan.org/spring-get-bean-in-code/
						context.close();
						break;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aspect;
	}
}