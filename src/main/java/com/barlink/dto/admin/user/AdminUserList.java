package com.barlink.dto.admin.user;

import java.time.LocalDateTime;

import javax.persistence.Entity;

import com.barlink.dto.common.CommonPage;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class AdminUserList  {
	
	
	@ApiModelProperty(name="email", example = "test@barlink.co.kr", value="검색할 유저의 이메일 주소",required = false)
	private String email;
	
	@ApiModelProperty(name="nickName", example = "으랏차차", value="검색할 유저의 닉네임",required = false)
	private String nickName;
	
	@ApiModelProperty(name="startDate",example = "2021-10-01", value="기간별 가입자 시작일",required = false)
	private String startDate;
	
	@ApiModelProperty(name="endDate",example = "2021-12-10", value="기간별 가입자 시작일",required = false)
	private String endDate;
	
}
