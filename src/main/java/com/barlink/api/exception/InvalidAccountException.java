package com.barlink.api.exception;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

public class InvalidAccountException extends AuthenticationException{

	public InvalidAccountException(String msg) {
		super(msg);
	}
	
	public InvalidAccountException(String msg,HttpServletResponse res) {
		super(msg);
		res.addHeader("message", msg);
		res.addIntHeader("serverStatus",404);
	}
	
	
}
