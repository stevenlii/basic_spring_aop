package com.paymoon.basic.spring.aop.demo.aspect.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.paymoon.basic.commons.exception.BaseException;
import com.paymoon.basic.spring.aop.demo.ServiceResult;
import com.paymoon.basic.spring.aop.demo.aspect.IAspect;
import com.paymoon.basic.spring.aop.demo.aspect.IParam;

@Service//@Component("cmdbCIBizExtendImpl")
@IParam(args = "teacher")
public class AspectTeacher implements IAspect{

	@Override
	public ServiceResult beforeHello(String type, Map<String, String> context) throws BaseException {
		System.out.println("AspectTeacher,beforeHello,type:"+type);
		return null;
	}

	@Override
	public ServiceResult afterHello(String type, Map<String, String> context, Map<String, Object> result) {
		System.out.println("AspectTeacher,afterHello,type:"+type);
		return null;
	}}