package com.barlink.dto.user.inpt;

import java.util.Arrays;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoriteList {
	
	@ApiModelProperty(name="관심글 번호 리스트",example = "[1,2,3]")
	private long[] favoriteList;
	
}
