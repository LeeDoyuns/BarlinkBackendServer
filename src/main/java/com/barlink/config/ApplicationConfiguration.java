package com.barlink.config;

import java.util.Collections;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.barlink.config.filter.SessionCheckFilter;
import com.barlink.config.filter.UserLoginFilter;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;

/**
 * 필터/세션 관련 설정
 * @author LeeDoYun
 *
 */
@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer{

	private static Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);
	
	@Bean
	 public ServletContextInitializer clearJsession() {
        return new ServletContextInitializer() {
        	
        	//최초 시작 시 세션 제거. 세션 생성때 JSESSIONID가 생성됨.
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
               servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
               SessionCookieConfig sessionCookieConfig=servletContext.getSessionCookieConfig();
               sessionCookieConfig.setHttpOnly(true);
            }
        };
    }
	
	//필터 등록. 해당 url을 입력했을때만 loginFilter가 동작하도록 함. 
	@Bean
	public FilterRegistrationBean<UserLoginFilter> getLoginFilter() {
		FilterRegistrationBean<UserLoginFilter> registrationBean = new FilterRegistrationBean(new UserLoginFilter());
		//sessionCheckFilter 먼저 동작하도록 한다.
		registrationBean.setOrder(2);
		
		//forwarding 하는경우, request하는 경우에 필터를 타도록 한다.
		registrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
		
		//pattern 지정
		registrationBean.addUrlPatterns("/api/user/loginResult");
		
		return registrationBean;
	}
	
	/*
	@Bean
	public FilterRegistrationBean<SessionCheckFilter> getSessionFilter() {
		FilterRegistrationBean<SessionCheckFilter> registrationBean = new FilterRegistrationBean(new SessionCheckFilter());
		//sessionCheckFilter 먼저 동작하도록 한다.
		registrationBean.setOrder(1);
		
		//forwarding 하는경우, request하는 경우에 필터를 타도록 한다.
		registrationBean.setDispatcherTypes(DispatcherType.FORWARD, DispatcherType.REQUEST);
		
		//pattern 지정
		registrationBean.addUrlPatterns("/api/*");
		
		return registrationBean;
	}*/
	

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") //모든 요청에 대해서 허용할 오리진들
//				.allowedOrigins("http://3.36.66.69","http://back.barlink.co.kr/","http://back.barlink.co.kr:8080","http://barlink.co.kr")
//				.allowedOrigins("http://locahlost:8000", "http://localhost:8001","https://barlink.co.kr","192.168.81.1:8080","http://3.36.66.69:8080/"); 
			.allowedOriginPatterns("*");
		
	}

}
