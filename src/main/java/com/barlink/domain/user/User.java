package com.barlink.domain.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.barlink.domain.drink.DrinkDetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * user기본 정보(session용)
 * 최초생성 : 2021-05-초반?
 * @author Doyun
 */

@Entity(name="users")
@Data
@DynamicInsert	/*default값이 지정되어있는 컬럼의 경우 null이여도 insert가능하게 함*/
@DynamicUpdate	/*default값이 지정되어있는 컬럼의 경우 null이여도 update가능하게 함*/
public class User  implements Serializable{
	private static final long serialId = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@ApiModelProperty(notes = "유저seq",  name = "userId" )
	@Column(name = "USER_ID")
	private Long userId;
	
	@Column(name="EMAIL", nullable = false, unique=true)
	@ApiModelProperty(notes = "유저 아이디", name = "email")
	private String email;
	
	@Column(name="NICK_NAME", nullable = false, unique=false)
	@ApiModelProperty(notes = "닉네임", name = "nickName")
	private String nickName;

	@Column(name="PASSWORD", nullable = false, unique=false)
	@ApiModelProperty(notes = "패스워드", name = "password")
	private String password;
	
	@Column(name="USER_ROLE", nullable = true, unique=false)
	@ApiModelProperty(notes = "유저 권한", name = "userRole")
	private String userRole;
	
	@Column(name="CREATE_PAGE", nullable = false, unique=false)
	@ApiModelProperty(notes = "생성페이지", name = "createPage")
	private String createPage;
	
	
	@Column(name="USE_STATUS", nullable = true, unique=false)
	@ApiModelProperty(notes = "사용여부", name = "useStatus")
	private String useStatus;
	
	@Column(name="REFRESH_TOKEN" ,nullable = true,unique = false)
	@ApiModelProperty(notes="refreshToken",name="userToken")
	private String userToken;

	
	/**
	 * 생성일
	 */
	@Column(name="CREATE_DATE",nullable = true)
	private LocalDateTime createDate;
	
	
	/**
	 * 최종 수정일
	 */
	@Column(name="UPDATE_DATE",nullable = true)
	private LocalDateTime updateDate;
	
	
	

	@Override
	public String toString() {
		return "User [userId=" + userId + ", email=" + email + ", nickName=" + nickName + ", password=" + password
				+ ", userRole=" + userRole + ", createPage=" + createPage + ", useStatus=" + useStatus + ", userToken="
				+ userToken + "]";
	}

	public String userInfo() {
		return "UserDTO [userId=" + userId + ", userId=" + email + ", nickName=" + nickName + ", password="
				+ password + ", userRole=" + userRole + ", createPage=" + createPage + "]";
	}
	
	
	
	
	/**************************************************************************************************/
	
	
	
	
	
	
	
	
	
	
}
