package com.barlink.dto.user;

import lombok.Data;

/**
 * 클라이언트단에 보낼 유저 정보
 * @author LeeDoyun
 *
 */
@Data
public class UserInfoDTO {

	//닉네임
	private String nickName;
	
	//이메일 주소
	private String email;
	
	//권한
	private String userRole;
	
	
	
	
	
	
}
