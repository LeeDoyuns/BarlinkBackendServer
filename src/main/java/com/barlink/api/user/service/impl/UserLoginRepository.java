package com.barlink.api.user.service.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.barlink.domain.user.User;


/**
 * LoginRepository
 * @author LeeDoyun
 *
 */
public interface UserLoginRepository extends JpaRepository<User, Object>{


	/**
	 * @desc email(이메일주소)로 유저 정보 탐색
	 * @param userId
	 * @return userVO
	 */
	User findByEmail(String email);

	
	/**
	 * @param passwd 
	 * @desc email(이메일주소),패스워드(passwd)로 유저 정보 탐색
	 * @param userId,passwd
	 * @return userVO
	 */
	User findByEmailAndPassword(String email, String passwd);


	/**
	 * userId(seq값)으로 조회
	 * @param i
	 * @return
	 */
	User findByUserId(int userId);


	/**
	 * DB에 저장된 refreshToken으로 유저 조회
	 * @param refreshToken
	 * @return
	 */
	User findByUserToken(String refreshToken);

	/**
	 * DB에 저장된 email으로 유저 조회. 현재 사용중인 계정만 조회한다.
	 * @param refreshToken
	 * @return
	 */
	User findByEmailAndUseStatus(String email, String string);

	
	/**
	 * DB에 저장된 RefreshToken값과 토큰 비교. 토큰을 parsing한 ID값도 같이 비교.
	 * @param userId
	 * @param refreshToken
	 * @return
	 */
	Optional<User> findByUserIdAndUserToken(long userId, String refreshToken);



	
}
