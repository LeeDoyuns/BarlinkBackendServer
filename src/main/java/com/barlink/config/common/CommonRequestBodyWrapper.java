package com.barlink.config.common;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * 자동으로 wrapper에서 requestBody를 반환해주는 클래스
 */
@Component
public class CommonRequestBodyWrapper {
	
	Logger logger = LoggerFactory.getLogger(CommonRequestBodyWrapper.class);
	
	public CommonRequestBodyWrapper() {}
	
	
	/**
	 * Body데이터를 JSONObject의 형태로 반환하는 메소드.
	 * @param (RequestBodyWrapper)request
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject getBodyData(RequestBodyWrapper request) throws IOException, ParseException {
		String data = request.getRequestBody();
		
		JSONParser parser = new JSONParser();
		
		return (JSONObject)parser.parse(data);
	}
	
	/**
	 * filter에 파라미터로 들어온 ServletRequest 객체를 Body데이터를 읽을 수 있는 RequestBodyWrapper로 반환해주는 메소드
	 * @param (ServletRequest)request
	 * @return
	 * @throws IOException
	 */
	public RequestBodyWrapper getWrapper(ServletRequest request) throws IOException {
		
		return new RequestBodyWrapper((HttpServletRequest)request);
	}
	
	
	/**
	 * filter에 파라미터로 들어온 ServletRequest 객체에서 RequestBody 데이터를 JSONObject 형태로 반환해주는 메소드
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public JSONObject getBodyData(ServletRequest request) throws IOException, ParseException {
		RequestBodyWrapper wrapper = new RequestBodyWrapper((HttpServletRequest)request);
		String data = wrapper.getRequestBody();
		
		if(data==null) {
			return null;
		}

		
		JSONParser parser = new JSONParser();
				
		return (JSONObject)parser.parse(data);
	}
	
	
	public JSONObject getBodyDataReqBodyWrapper(RequestBodyWrapper request) throws IOException, ParseException {
		String data = request.getRequestBody();
		JSONParser parser = new JSONParser();
		
		return (JSONObject)parser.parse(data);
	}
	
	public JSONObject getBodyDataByString(String bodyData) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(bodyData);
	}
	
	public String getRequestParam(String paramName,HttpServletRequest request) throws IOException {
		RequestBodyWrapper wrapper = new RequestBodyWrapper((HttpServletRequest)request);
		String data = wrapper.getParameter(paramName);
		return data;
	}
	


}
