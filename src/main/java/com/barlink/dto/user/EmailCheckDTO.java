package com.barlink.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EmailCheckDTO {
	@ApiModelProperty(name="이메일 주소", example = "test@barlink.co.kr")
	private String email;
	
	@ApiModelProperty(name="인증코드", example = "326215")
	private String code;

}
