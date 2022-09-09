package com.barlink.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 공통 페이징
 * @author LeeDoYun
 *
 */
@Data
public abstract class CommonPage {

	@ApiModelProperty(name="page", example = "1", value = "탐색할 페이지 번호. 필수 입력",required = true)
	private int page;
	
	@ApiModelProperty(name="pageSize", example = "10", value="한번에 탐색할 요소의 개수. 필수 입력",required = true)
	private int pageSize;
}
