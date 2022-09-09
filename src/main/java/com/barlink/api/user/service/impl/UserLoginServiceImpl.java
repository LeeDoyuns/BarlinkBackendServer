package com.barlink.api.user.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.barlink.api.user.service.UserLoginService;
import com.barlink.config.common.CommonEncoder;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.common.RequestBodyWrapper;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.user.User;
import com.barlink.dto.user.UserInfoDTO;

/**
 * 유저 로그인 등 관련 로직을 처리하는 Service.
 * @author LeeDoYun
 *
 */
@Service(value = "userLoginService")
@SessionAttributes(value = "userSession")
public class UserLoginServiceImpl implements UserLoginService {

	@Autowired 
	UserLoginRepository userRepo;
	
	@PersistenceContext
	EntityManager emgr;
	
	@Autowired
	private CommonRequestBodyWrapper commonWrapper;
	
	/*
	 * password encoder
	 */
	private CommonEncoder encoder = new CommonEncoder();
	

	
	/**
	 * 유저 데이터 select
	 */
	@Override
	public User findByEmail(String userId, String passwd) {
		return userRepo.findByEmail(userId);
	}

	

	@Override
	public User findByEmail(String userId) {
		return userRepo.findByEmail(userId);
	}



	@Override
	public void updateRefreshToken(User user) {
		userRepo.save(user);
	}


	@Override
	public boolean checkAccessToken(Map param) {
		
		String refreshToken = (String) param.get("refreshToken");
		
			//1. refreshToken
		
			Map checkMap = JwtUtil.getRefreshTokenBody(refreshToken);
			String domain = (String) checkMap.get("iss");
			if(!"refresh_barlink".equals(domain)) {
				return false;
			}
			
		
			String userId = JwtUtil.getRefreshSubject(refreshToken);
			Optional<User> users = userRepo.findById(Long.parseLong(userId));
			User user = users.get();
			
			if(user==null) {
				return false;
			}
			
			
		return true;
	}



	@Override
	public Optional<User> findById(int userId) {
		return userRepo.findById(userId);
	}



	@Override
	public void updateNickName(User user) {
		userRepo.save(user);
	}



	@Override
	public UserInfoDTO parsingUserInfo(User user) {
		UserInfoDTO userInfo = new UserInfoDTO();
		userInfo.setNickName(user.getNickName());
		userInfo.setEmail(user.getEmail());
		userInfo.setUserRole(user.getUserRole());
		return userInfo;
	}



	@Override
	public Optional<User> findById(long userId) {
		return userRepo.findById(userId);
	}



	@Override
	public Optional<User> findByIdAndRefreshToken(long userId, String refreshToken) {
		return userRepo.findByUserIdAndUserToken(userId,refreshToken);
	}




}
