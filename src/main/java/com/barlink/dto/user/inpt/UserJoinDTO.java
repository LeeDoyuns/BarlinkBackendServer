package com.barlink.dto.user.inpt;


import javax.validation.constraints.NotBlank;


import com.barlink.domain.user.User;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserJoinDTO {

	@NotBlank(message = "이메일 주소를 입력해주세요.")
	@ApiModelProperty(name="email",example = "awesome@barlink.co.kr")
	private String email;
	
	@ApiModelProperty(name="nickName",example = "어썸투게더")
	@NotBlank(message = "닉네임을 입력해주세요.")
	private String nickName;
	
	@ApiModelProperty(name="password",example = "test")
	@NotBlank(message = "패스워드를 입력해주세요.")
	private String password;
	
	@ApiModelProperty(name="createPage",example = "JOIN_PAGE",notes = "회원가입한 페이지 명, 추후 부정한 방법을 통해 접근했을 때 추적을 위한 준비")
	@NotBlank(message = "생성 페이지명을 입력해주세요.")
	private String createPage;
	
	
	
	
	public User toEntity() {
		User user = new User();
		user.setEmail(this.email);
		user.setNickName(this.nickName);
		user.setPassword(this.password);
		user.setCreatePage(this.createPage);
		
		return user;
		
	}
	
}
