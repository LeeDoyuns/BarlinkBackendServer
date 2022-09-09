package com.barlink.dto.user.inpt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmail {
	
	@ApiModelProperty(name = "email", example = "test@barlink.co.kr")
	private String email;

}
