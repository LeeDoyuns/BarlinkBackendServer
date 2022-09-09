package com.barlink.config.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.barlink.config.common.CommonEncoder;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.common.ExpectURLList;
import com.barlink.config.common.RequestBodyWrapper;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.user.User;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * SessionFilter
 * 일부 요청에 대해 세션(암호화된 userSeq)을 체크함.
 * 일부 url요청에 대해서는 필터로 체크하지 않음. 제외할 url은 ExpectURLList.java에 등록.
 * @author LeeDoyun
 *
 */
public class SessionCheckFilter implements Filter{
	
	Logger logger = LoggerFactory.getLogger("sessionLogger");

	private ExpectURLList url = new ExpectURLList();
	
	@Autowired
	private CommonEncoder encoder;
	
	@Autowired
	private CommonRequestBodyWrapper commonWrapper = new CommonRequestBodyWrapper();
	

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}
	
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		JSONParser parser = new JSONParser();
		
		RequestBodyWrapper req = commonWrapper.getWrapper(request);
		HttpServletResponse res = (HttpServletResponse) response;
		
		
		
		String path = req.getServletPath();
		//url체크 로직. session검증(로그인 정보)이 필요없는 url에서만 세션 검증한다.
		ArrayList<String> urlList = url.getUrlList();
		// 여기서 pathVariable(id) Check가 안되서 아래처럼 조건 추가해야할듯? (코드가 맘에 안드는데.. 딱히 방법이 생각이 안나네요)

		if(!urlList.contains(path) && !path.contains("names")) {
			logger.info("======================sessionFilter========================");
		try {
			/*토큰이 없다면*/
			if(req.getHeader("Authorization") == null) {
				res.setStatus(res.SC_NOT_ACCEPTABLE);
				res.addHeader("message", "need Login or userToken");
				return;
			}
			
			String userToken = req.getHeader("Authorization").replace("Bearer ", "");
			
			//token이 존재하지 않는다면
			if( userToken==null || "".equals(userToken) ) {
				res.setStatus(res.SC_NOT_ACCEPTABLE);
				res.addHeader("message", "need Login or userToken");
				
			}else {
				//token이 존재한다면 parsing 해서 userId로 유저 정보 찾음
				Map<String, Object> token = JwtUtil.getTokenBody(userToken);
				
				String domain = (String) token.get("iss");
				Map userInfoMap = (Map) token.get("access");
				String userRole = (String) userInfoMap.get("userRole");
				
				req.setAttribute("userInfoMap", userInfoMap);
				
				//권한 체크
				if(path.contains("admin") && !userRole.equals("ADMIN")) {
					res.setStatus(res.SC_NOT_ACCEPTABLE);
					res.addHeader("message", "invalid User");
				}
				
			}
		}catch(ExpiredJwtException e) {
			//토큰이 만료된 경우 처리
			e.printStackTrace();
			res.setStatus(res.SC_UNAUTHORIZED);
			res.addHeader("message", "Invalid UserInfo Or AccessToken is Expired");
			return;
		}catch(NullPointerException e) {
			res.setStatus(res.SC_FORBIDDEN);
			res.addHeader("message", "403 Error");
			return;
		}
		
			
		}
		
		chain.doFilter(req, res);
	}
	
	@Override
	public void destroy() {
		Filter.super.destroy();
	}
	

}
