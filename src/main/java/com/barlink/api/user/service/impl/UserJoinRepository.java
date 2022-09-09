package com.barlink.api.user.service.impl;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barlink.domain.user.User;

/**
 * JoinRepository
 * @author LeeDoyun
 *
 */
@Repository
public interface UserJoinRepository  extends JpaRepository<User,Object>{

	/**
	 * 이메일로 정보 조회
	 * @param email
	 * @return
	 */
	User findByEmail(String email);

	/**
	 * 이메일 중복여부 조회
	 * @param email
	 * @param status
	 * @return
	 */
	User findByEmailAndUseStatus(String email, String status);

	/**
	 * 닉네임 중복여부 조회
	 * @param nickName
	 * @return
	 */
	List<User> findByNickName(String nickName);
	

}
