package com.barlink.config.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.barlink.api.user.service.UserLoginService;
import com.barlink.api.user.service.impl.UserLoginServiceImpl;
import com.barlink.config.common.CommonEncoder;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.common.RequestBodyWrapper;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.user.User;
import com.barlink.dto.user.UserInfoDTO;



/**
 * 로그인 필터
 * 로그인 요청이 들어왔을 때 토큰을 생성함.
 * @author LeeDoyun
 *
 */
//@WebFilter(filterName="loginFilter"  ,urlPatterns = "/api/user/login")
public class UserLoginFilter implements Filter {

	Logger logger = LoggerFactory.getLogger("sessionLogger");
	
	
	@Autowired
	private CommonRequestBodyWrapper commonWrapper = new CommonRequestBodyWrapper();
	
	
	public UserLoginFilter() {
		super();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		logger.info("------------LoginFilter------------");
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest)request;
		

		try {
			if(req.getAttribute("user")==null) {
				res.setStatus(res.SC_FORBIDDEN);
				res.addHeader("message","Not found this user");
			}else {
				User user = (User) req.getAttribute("user");
				//refreshToken이 존재하는 경우, 이미 로그인중인 것으로 간주한다.
					String password = (String) req.getAttribute("inputPassword");
					BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
					
					if(encoder.matches(password, user.getPassword())) {

						//유저에게 return할 유저 정보.
						UserInfoDTO userInfoDTO = new UserInfoDTO();
						userInfoDTO.setEmail(user.getEmail());
						userInfoDTO.setNickName(user.getNickName());
						userInfoDTO.setUserRole(user.getUserRole());
						
						String token = JwtUtil.createToken(String.valueOf(user.getUserId()), userInfoDTO);
						String refresh = JwtUtil.createRefreshToken(String.valueOf(user.getUserId()), userInfoDTO);
						
						//refreshToken
						user.setUserToken(refresh);
						
						
						req.setAttribute("accessToken", token);
						req.setAttribute("userInfo", userInfoDTO);
						req.setAttribute("user", user);
						res.setStatus(res.SC_OK);
						res.addHeader("message", "success");
					}else {
						
						res.setStatus(res.SC_FORBIDDEN);
						res.addHeader("message","Not found this user");
					}
				}	
			
		}catch(Exception e) {
			e.printStackTrace();
			res.setStatus(res.SC_FORBIDDEN);
			res.addHeader("message","Not found this user");
		}
	
		
		chain.doFilter(req, res);
		
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}
	
	
	
}
