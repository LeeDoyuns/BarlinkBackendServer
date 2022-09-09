package com.barlink.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.barlink.config.common.CommonEncoder;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.filter.UserLoginFilter;
import com.barlink.config.jwt.JwtUtil;


/**
 * 20210513 이도윤 작성
 * SpringSecurity 관련 설정
 * 패스워드암호화 및 security 로그인 처리
 * @author Doyun
 *
 */
@Configuration
@EnableWebSecurity
public class SpringSecurity extends WebSecurityConfigurerAdapter {
	
	Logger logger = LoggerFactory.getLogger(SpringSecurity.class);
	
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable()
			.csrf().disable()
			.headers().frameOptions().disable()
			.and()
			//세션 사용 안함
			 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			//폼 로그인 사용 안함
			.formLogin().disable()
			;
		
	}
	
	

	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/swagger/**");
	}




	//패스워드 단방향암호화
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CommonEncoder encoder() {
		return new CommonEncoder();
	}
	
	@Bean
	public CommonRequestBodyWrapper commonWrapper() {
		return new CommonRequestBodyWrapper();
	}
	
}
