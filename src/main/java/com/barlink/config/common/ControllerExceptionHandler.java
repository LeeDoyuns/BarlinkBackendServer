package com.barlink.config.common;

import java.net.BindException;
import java.util.HashMap;
import java.util.Map;

import com.barlink.api.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * MethodArgumentValidException handler
 * @author LeeDoyun
 *
 */

@ControllerAdvice
public class ControllerExceptionHandler {

	private Logger logger=  LoggerFactory.getLogger(ControllerExceptionHandler.class);
	
	
	/**
	 * 에러 핸들러
	 */
	/**
	 * 컨트롤러 내에서 DTO에 parameter validation이 충족되지 않을경우 해당 에러메세지 리턴
	 * @param parameter
	 * @param bindingResult
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected Object customMethodArgumentNotValidException(BindingResult bindingResult) {
		
		String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
		
		Map map = new HashMap<>();
		map.put("message", message);
		
		return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(RestException.class)
	protected ResponseEntity<Map<String, Object>> restException(RestException e) {
		Map<String, Object> resBody = new HashMap<>();
		resBody.put("message", e.getMessage());

		return new ResponseEntity<>(resBody, e.getStatus());
	}
	
}
