package com.barlink.api.user.service;

import java.util.Map;
import java.util.Optional;

import com.barlink.domain.user.User;
import com.barlink.dto.user.UserInfoDTO;

/**
 * Login
 * @author LeeDoyun
 *
 */
public interface UserLoginService {


	User findByEmail(String userId, String passwd);



	User findByEmail(String userId);



	void updateRefreshToken(User user);


	boolean checkAccessToken(Map param);



	Optional<User> findById(int parseInt);



	void updateNickName(User user);



	UserInfoDTO parsingUserInfo(User user);



	Optional<User> findById(long userId);



	Optional<User> findByIdAndRefreshToken(long parseLong, String refreshToken);



}
