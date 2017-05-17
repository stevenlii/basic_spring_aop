package com.paymoon.basic.spring.aop.demo.aspect;

import java.util.Map;

import com.paymoon.basic.commons.exception.BaseException;
import com.paymoon.basic.spring.aop.demo.ServiceResult;

public interface IAspect {

	public ServiceResult beforeHello(String type,Map<String,String> context)throws BaseException;
	
	public ServiceResult afterHello(String type,Map<String,String>  context,Map<String, Object> result);
	
}
