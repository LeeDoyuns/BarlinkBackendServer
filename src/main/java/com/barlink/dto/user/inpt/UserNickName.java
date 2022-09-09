package com.barlink.dto.user.inpt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNickName {

	@ApiModelProperty(name="닉네임",example ="으랏차차" )
	private String nickName;
}
