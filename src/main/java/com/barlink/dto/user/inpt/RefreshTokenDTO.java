package com.barlink.dto.user.inpt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDTO {

	@ApiModelProperty(name="refreshToken" , example = "eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJzdWIiOiI3IiwiaXNzIjoicmVmcmVzaF9iYXJsaW5rIiwicGVybWlzc2lvbiI6dHJ1ZSwiYWNjZXNzIjp7Im5pY2tOYW1lIjoiYXdlc29tZVRlc3QhQCIsImVtYWlsIjoidGVzdEBiYXJsaW5rLmNvLmtyIiwidXNlclJvbGUiOiJBRE1JTiJ9LCJleHAiOjE2MzM3NzcwMDJ9._vdhp72klU-lXqVNkmq6Icl_7CtLDM4w6CXZ7iDe4bk")
	private String refreshToken;
}
