package com.paymoon.basic.spring.aop.demo.service.Impl;

import org.springframework.stereotype.Service;

import com.paymoon.basic.spring.aop.demo.service.IHello;

@Service
public class HelloTeacher implements IHello {

    public void sayHello() {
        System.out.println("I'm teacher, I can say hello to students");
    }

	@Override
	public void sayHello(String helloParam) {
		 System.out.println("I'm teacher, I can say hello to students, here is helloParam: >"+helloParam);
		
	}

}