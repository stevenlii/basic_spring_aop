package com.paymoon.basic.spring.aop.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.paymoon.basic.spring.aop.demo.service.IHello;

public class App {
	private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
    	
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("root-context.xml");

//        IHello userService = context.getBean(IHello.class);
        IHello userService =  null;
        userService =  context.getBean("helloTeacher",IHello.class);
        userService.sayHello("teacher");
        userService =  context.getBean("helloStudent",IHello.class);
        userService.sayHello("student");
        context.close();
    }
}
