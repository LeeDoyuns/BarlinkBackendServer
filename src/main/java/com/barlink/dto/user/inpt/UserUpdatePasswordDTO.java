package com.barlink.dto.user.inpt;

import org.springframework.beans.factory.annotation.Autowired;

import com.barlink.config.common.CommonEncoder;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 유저가 패스워드를 업데이트 할 때 입력할 DTO
 * @author LeeDoYun
 *
 */
@Getter
@Setter
public class UserUpdatePasswordDTO {
	
	
	@ApiModelProperty(name = "변경 전 기존 패스워드", example = "barlink12345!")
	private String beforePassword;
	
	@ApiModelProperty(name = "변경 할 패스워드", example = "updPwd12345!")
	private String updatePassword;
	
	
	@ApiIgnore
	public String encodeBeforePwd(String beforePassword) {
		
		CommonEncoder encoder = new CommonEncoder();
		
		String encodedPassword = null;
		encodedPassword = encoder.encode(beforePassword);		
		
		return encodedPassword;
	}

}
