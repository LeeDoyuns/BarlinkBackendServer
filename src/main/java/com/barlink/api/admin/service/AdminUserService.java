package com.barlink.api.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.barlink.domain.user.User;
import com.barlink.dto.admin.user.AdminUserInfo;
import com.barlink.dto.admin.user.AdminUserList;
import com.barlink.dto.board.UserBuyListDto;
import com.barlink.dto.user.AdminUserInfoDTO;

@Repository
public interface AdminUserService {

	User chenageBycryptPassword(User user, String changePassword);

	void makeTestUser();

	Page<AdminUserInfoDTO> selectAllUser(AdminUserList param, PageRequest page);

	Page<UserBuyListDto> selectUserBuyList(long userId, PageRequest pages);

	User updateUserInfo(User user, AdminUserInfo info);

	Map<String, Object> selectAllUser(AdminUserList param);

	List<UserBuyListDto> selectUserBuyList(long userId);

	Map<String, Object> selectUserCount();


}
