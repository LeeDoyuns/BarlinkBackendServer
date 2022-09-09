package com.barlink.dto.admin.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserPassword {
	
	@ApiModelProperty(name="password",notes="강제 변경할 비밀번호", example = "12345")
	private String password;

}
