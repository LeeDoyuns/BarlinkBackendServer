package com.barlink.dto.user;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * 찜목록 리스트 (노출용)
 * @author LeeDoYun
 * @date 2021.07.11
 *
 */
@Builder
@ToString
public class UserFavoriteDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public long drinkDetailId;
	
	public String drinkName;
	
	public int drinkVolumn;
	
	public String drinkYear;
	
	public long favId;
	
	public String storeName;
	
	public String address;
	
	public String buyDate;
	
	public String nickName;
	
	public Integer cost;
	
	
}
