package com.paymoon.basic.spring.aop.demo.service.Impl;

import org.springframework.stereotype.Service;

import com.paymoon.basic.spring.aop.demo.service.IHello;

@Service
public class HelloStudent implements IHello {

    public void sayHello() {
        System.out.println("hello,I'm students");
    }

	@Override
	public void sayHello(String helloParam) {
		 System.out.println("hello,I'm students,  here is helloParam: >"+helloParam);
		
	}

}