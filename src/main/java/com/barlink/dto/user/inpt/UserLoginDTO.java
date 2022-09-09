package com.barlink.dto.user.inpt;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
 * Login 관련 DTO
 * LEEDOYUN
 */

@Data
public class UserLoginDTO {

		@NotBlank(message = "등록된 이메일 주소를 입력해주세요.")
		@ApiModelProperty(name = "이메일 주소", example = "test@barlink.co.kr / AwesomeAdminBar")
		private String email;
		
		@NotBlank(message ="패스워드를 입력해주세요.")
		@ApiModelProperty(name="패스워드",example = "awesomeTest!@ / BarlinkAdmin2020!)#)")
		private String password;
		
		
}
