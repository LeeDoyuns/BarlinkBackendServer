package com.barlink.config.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * 인코딩(패스워드,유저seq) 관련 인코딩 클래스.
 * 사용 예제는 UserLoginServiceImpl.java 파일 참조. 
 * @author LeeDoYun
 *
 */

@Component
public class CommonEncoder implements PasswordEncoder{
	
	private BCryptPasswordEncoder encoder;
	
	public CommonEncoder() {
		this.encoder=new BCryptPasswordEncoder();
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return this.encoder.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence originPassword, String encodedPassword) {
		return this.encoder.matches(originPassword, encodedPassword);
	}

}
