package com.barlink.api.admin.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.barlink.api.admin.service.AdminUserService;
import com.barlink.api.user.service.UserJoinService;
import com.barlink.config.common.CommonEncoder;
import com.barlink.config.common.CommonLocalDateParser;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.drink.QDrink;
import com.barlink.domain.drink.QDrinkCategory;
import com.barlink.domain.drink.QDrinkDetail;
import com.barlink.domain.drink.QVolume;
import com.barlink.domain.store.QStore;
import com.barlink.domain.user.QUser;
import com.barlink.domain.user.QUserFavoriteDrinkDetail;
import com.barlink.domain.user.User;
import com.barlink.dto.admin.user.AdminUserInfo;
import com.barlink.dto.admin.user.AdminUserList;
import com.barlink.dto.board.UserBuyListDto;
import com.barlink.dto.user.AdminUserInfoDTO;
import com.barlink.dto.user.UserInfoDTO;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
public class AdminUserServiceImpl implements AdminUserService{
	
	@Autowired
	private AdminUserRepository repo;
	
	@Autowired
	private CommonEncoder encoder;
	
	/*QueryDSL을 사용하기 위한 필드*/
	@PersistenceContext
	private EntityManager em;
	private JPAQueryFactory factory;
	
	@Autowired
	private UserJoinService userJoinService;
	
	

	@Override
	@Transactional
	public User chenageBycryptPassword(User user,String changePassword) {
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		changePassword = encoder.encode(changePassword);
		user.setPassword(changePassword);
		user.setUpdateDate(LocalDateTime.now());	//업데이트 시각
		
		try {
			user = repo.save(user);
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
			
		}
		
		return user;
	}


	@Override
	public Page<AdminUserInfoDTO> selectAllUser(AdminUserList param, PageRequest page) {
		
		factory = new JPAQueryFactory(em);
		
		
		QUser user = QUser.user;
		
		
		QueryResults<AdminUserInfoDTO> results  = factory.select(Projections.fields(AdminUserInfoDTO.class,
				user.userId,
				user.nickName,
				user.email,
				user.userRole
				))
			.from(user)
			.where(email(param.getEmail(),user),
					nickName(param.getNickName(), user),
					joinDate(param,user) //가입일 기준으로 조회
					   )
				.offset(page.getOffset())
				.limit(page.getPageSize())
				.orderBy(user.userId.asc())
				.fetchResults();
		
		return new PageImpl<>(results.getResults(), page, results.getTotal());
		
	}
	
	private BooleanExpression joinDate(AdminUserList data,QUser user) {
		//전부 비어있는 경우 where절을 타지 않는다.
		if((data.getStartDate() == null || !StringUtils.hasText(data.getStartDate())) 
			&& (data.getEndDate() == null || !StringUtils.hasText(data.getEndDate()))  ) {
			return null;
		}
		LocalDateTime startDt = null;
		LocalDateTime endDt = null;
		
		//날짜 지정을 했을때의 분기처리
		//1. 검색 시작일만 지정한 경우
		if((data.getStartDate() != null && StringUtils.hasText(data.getStartDate()))
				&& (data.getEndDate() == null && !StringUtils.hasText(data.getEndDate())) ) {
			
			startDt = CommonLocalDateParser.startDateTime(data.getStartDate());
			return user.createDate.after(startDt);
			
		}else if(	//2. 종료일만 지정된 경우
				
				(data.getEndDate() != null && StringUtils.hasText(data.getEndDate()))
				&& (data.getStartDate() == null && !StringUtils.hasText(data.getStartDate()))
				) {
			endDt = CommonLocalDateParser.endDateTime(data.getEndDate());
			return user.createDate.before(endDt);
			
		}else {
			startDt = CommonLocalDateParser.startDateTime(data.getStartDate());
			endDt = CommonLocalDateParser.endDateTime(data.getEndDate());
			
			return user.createDate.between(startDt, endDt);
			
		}
		
	}
	
	private BooleanExpression email(String email,QUser user) {
		if(email == null || !StringUtils.hasText(email)) {
			return null;
		}
		
		return user.email.contains(email);
		
	}
	
	private BooleanExpression nickName(String nickName,QUser user) {
		if(nickName == null || !StringUtils.hasText(nickName)) {
			return null;
		}
		
		return user.email.contains(nickName);
	}


	@Override
	public void makeTestUser() {
		for(int i=0;i<100;i++) {
			User user = new User();
			user.setEmail("testAccount"+i+"@test.com");
			user.setCreatePage("Admin - Java Application");
			user.setNickName("테스트계정"+i);
			user.setPassword("1234");
			userJoinService.insertUser(user);
		}
	}


	@Override
	@Transactional
	public Page<UserBuyListDto> selectUserBuyList(long userId, PageRequest page) {
		
		factory = new JPAQueryFactory(em);
		QUser user = QUser.user;
		QDrinkDetail detail = QDrinkDetail.drinkDetail;
		QStore store = QStore.store;
		QDrink drink = QDrink.drink;
		QDrinkCategory category = QDrinkCategory.drinkCategory;
		QUserFavoriteDrinkDetail dt = QUserFavoriteDrinkDetail.userFavoriteDrinkDetail;
		
		
		QueryResults<UserBuyListDto> list = factory.select(Projections.fields(UserBuyListDto.class, 
				detail.id,
				drink.drinkName,
				detail.age,
				detail.volume,
				detail.cost,
				detail.createdDate,
				detail.isAccess,
				store.storeName,
				store.basicAddress,
				store.detailAddress,
				store.callNumber
				))
		.from(detail)
		.innerJoin(detail.user(), user)
		.innerJoin(detail.store(),store)
		.innerJoin(detail.drink(),drink)
		.innerJoin(drink.drinkCategory(),category)
		.where(detail.user().userId.eq(userId))
		.fetchResults(); 
		
		
		return new PageImpl<>(list.getResults(), page, list.getTotal());
	}


	@Override
	@Transactional
	public User updateUserInfo(User user, AdminUserInfo info) {
		String changePassword = null;
		String nickName = null;
		String userRole = null;
		String useStatus = null;
		
		if(info.getPassword() != null) {
			changePassword = encoder.encode(info.getPassword());
		}else {
			changePassword = user.getPassword();
		}
		
		if(info.getNickName() != null) {
			nickName = info.getNickName();
		}else {
			nickName = user.getNickName();
		}
		
		if(info.getUserRole() != null) {
			userRole = info.getUserRole();
		}else {
			userRole= user.getUserRole();
		}
		
		if(info.getUseStatus() != null) {
			useStatus = info.getUseStatus();
		}else {
			useStatus = user.getUseStatus();
		}
		
		user.setPassword(changePassword);
		user.setNickName(nickName);
		user.setUserRole(userRole);
		user.setUseStatus(useStatus);
		user.setUpdateDate(LocalDateTime.now());	//업데이트 시각
		
		user = repo.saveAndFlush(user);
		
		return user;
	}


	@Override
	public Map<String, Object>  selectAllUser(AdminUserList param) {
		factory = new JPAQueryFactory(em);
		
		
		QUser user = QUser.user;
		QDrinkDetail detail = QDrinkDetail.drinkDetail;
		
		
		/**
		 * Projections.fields(AdminUserInfoDTO.class, 사용할 컬럼 필드 ) : 사용할 컬럼만 가져올 수 있음. DTO로 따로 변환해줄 필요 없음.
		 */
		QueryResults<AdminUserInfoDTO> results  = factory.select(Projections.fields(AdminUserInfoDTO.class,
																user.userId,
																user.nickName,
																user.email,
																user.userRole,
																ExpressionUtils.as(JPAExpressions.select(detail.id.count())
																									.from(detail)
																									.where(detail.user().eq(user))
																					, "userContent")
																))
			.from(user)
			.where(
					email(param.getEmail(),user),
					nickName(param.getNickName(), user),
					joinDate(param,user) //가입일 기준으로 조회
				   )
			.orderBy(user.userId.asc())
			.fetchResults();
		
		Map <String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("list", results.getResults());
		resultMap.put("count", results.getTotal());
		
		return resultMap;
	}


	@Override
	public List<UserBuyListDto> selectUserBuyList(long userId) {
		
		factory = new JPAQueryFactory(em);
		QUser user = QUser.user;
		QDrinkDetail detail = QDrinkDetail.drinkDetail;
		QStore store = QStore.store;
		QDrink drink = QDrink.drink;
		QDrinkCategory category = QDrinkCategory.drinkCategory;
		QUserFavoriteDrinkDetail dt = QUserFavoriteDrinkDetail.userFavoriteDrinkDetail;
		
		
		QueryResults<UserBuyListDto> list = factory.select(Projections.fields(UserBuyListDto.class, 
				detail.id,
				drink.drinkName,
				detail.age,
				detail.volume,
				detail.cost,
				detail.createdDate,
				detail.isAccess,
				store.storeName,
				store.basicAddress,
				store.detailAddress,
				store.callNumber
				))
		.from(detail)
		.innerJoin(detail.user(), user)
		.innerJoin(detail.store(),store)
		.innerJoin(detail.drink(),drink)
		.innerJoin(drink.drinkCategory(),category)
		.where(detail.user().userId.eq(userId))
		.fetchResults(); 
		
		List<UserBuyListDto> result = list.getResults();
		
		return result;
	}


	@Override
	public Map<String, Object> selectUserCount() {
		
		factory = new JPAQueryFactory(em);
		
		int totalUserCnt = repo.getTotalUserCount();
		
		LocalDateTime start = CommonLocalDateParser.todayStartDateTime();
		LocalDateTime end = CommonLocalDateParser.todayEndDateTime();
		
		int todayUserCnt = repo.getTodayUserCount(start,end);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("totalUserCnt", totalUserCnt);
		result.put("todayUserCnt", todayUserCnt);
		
		

		return result;
	}
	
	

}
