package com.barlink.api.user;

import org.junit.Test;

import com.barlink.api.user.service.impl.UserJoinServiceImpl;

public class EmailTest {
	
	
	
	@Test
	public void sendEmail() {
		UserJoinServiceImpl service = new UserJoinServiceImpl();
		
		boolean result = service.sendEmail("test@barlink.co.kr");
		System.out.println("@@@@@@@@"+result);
	}
	

}
