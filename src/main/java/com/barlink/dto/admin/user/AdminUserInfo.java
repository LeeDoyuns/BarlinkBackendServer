package com.barlink.dto.admin.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserInfo {
	
	@ApiModelProperty(name="userId", value="userSeq값", example = "1",required = false)
	private long userId;
	
	@ApiModelProperty(name="nickName",value="변경할 닉네임", example = "바링", required = false)
	private String nickName;
	
	@ApiModelProperty(name="password",value="변경할 패스워드", example = "testPwd", required = false)
	private String password;
	
	@ApiModelProperty(name="userRole", value="권한", example = "ADMIN/USER", required = false)
	private String userRole;
	
	@ApiModelProperty(name="useStatus", value="계정 사용 여부, N으로 바뀌면 동일 이메일로 가입이 가능해진다.", example = "Y/N", required = false)
	private String useStatus;

}
