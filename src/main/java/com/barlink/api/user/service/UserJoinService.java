package com.barlink.api.user.service;

import java.util.List;

import com.barlink.domain.user.User;
import com.barlink.dto.user.EmailCheckDTO;

/**
 * Join
 * @author LeeDoyun
 *
 */
public interface UserJoinService {

	User insertUser (User vo);

	User findByEmail(String email);

	boolean sendEmail(String to);

	boolean checkAuthCode(EmailCheckDTO check);

	List<User> findByNickName(String nickName);

}
