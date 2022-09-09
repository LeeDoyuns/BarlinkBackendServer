package com.barlink.api.user.service;

import java.util.Map;

import com.barlink.domain.user.User;

/**
 * Info
 * @author LeeDoyun
 *
 */
public interface UserInfoService {

	User selectUserInfo(String email);

	User updatePassword(User user, String password);

	Map parseingUserInfo(String string);

	User leaveUser(User user);

	boolean sendMail(String email);

	User selectUserInfo(String string, String encodeBeforePwd);

}
