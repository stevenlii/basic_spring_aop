package com.paymoon.basic.spring.aop.demo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ServiceResult implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String status = "failed";
	protected String code;
	protected String errorMessage;
	public ServiceResult(){}
	public ServiceResult(String status,String code,String errorMessage){
		this.status=status;
		this.code=code;
		this.errorMessage=errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
