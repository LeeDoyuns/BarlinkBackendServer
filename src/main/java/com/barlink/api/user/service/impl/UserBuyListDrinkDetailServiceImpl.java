package com.barlink.api.user.service.impl;

import com.barlink.api.drink.service.impl.DrinkDetailRepository;
import com.barlink.api.user.service.UserBuyListDrinkDetailService;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.drink.QDrink;
import com.barlink.domain.drink.QDrinkCategory;
import com.barlink.domain.drink.QDrinkDetail;
import com.barlink.domain.store.QStore;
import com.barlink.domain.user.QUser;
import com.barlink.domain.user.QUserFavoriteDrinkDetail;
import com.barlink.domain.user.User;
import com.barlink.dto.board.UserBuyListDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class UserBuyListDrinkDetailServiceImpl implements UserBuyListDrinkDetailService {

    @Autowired
    DrinkDetailRepository drinkDetailRepository;

    /*QueryDSL을 사용하기 위한 필드*/
	@PersistenceContext
	private EntityManager em;
	private JPAQueryFactory factory;
	
	
	
	
    @Override
    @Transactional
    public  Page<UserBuyListDto> selectUserBuyList(User user,PageRequest page) {
    	
    	
    	long userId = user.getUserId();
    	
    	factory = new JPAQueryFactory(em);
		QUser users = QUser.user;
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
		.innerJoin(detail.user(), users)
		.innerJoin(detail.store(),store)
		.innerJoin(detail.drink(),drink)
		.innerJoin(drink.drinkCategory(),category)
		.where(detail.user().userId.eq(userId))
		.fetchResults(); 
		
    	
		return new PageImpl<>(list.getResults(), page, list.getTotal());
    }
}
