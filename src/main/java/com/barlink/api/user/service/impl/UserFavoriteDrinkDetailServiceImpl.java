package com.barlink.api.user.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barlink.api.drink.service.DrinkCategoryService;
import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.drink.service.DrinkService;
import com.barlink.api.user.service.UserFavoriteDrinkDetailService;
import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkCategory;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.drink.QDrink;
import com.barlink.domain.drink.QDrinkDetail;
import com.barlink.domain.store.QStore;
import com.barlink.domain.user.QUser;
import com.barlink.domain.user.QUserFavoriteDrinkDetail;
import com.barlink.domain.user.User;
import com.barlink.domain.user.UserFavoriteDrinkDetail;
import com.barlink.dto.drink.MainDrinkInfosDto;
import com.barlink.dto.user.UserFavoriteDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 찜기능 처리 Service
 * @author LeeDoYun
 *
 */
@Service
public class UserFavoriteDrinkDetailServiceImpl implements UserFavoriteDrinkDetailService {
	
	@Autowired
	private UserFavoriteDrinkDetailRepository favRepo;
	
	@Autowired
	private DrinkDetailService drinkDetailService;
	
	@Autowired
	private DrinkCategoryService categoryService;
	
	@Autowired
	private DrinkService drinkService;
	
	/*QueryDSL을 사용하기 위한 필드*/
	@PersistenceContext
	private EntityManager em;
	

	@Override
	@Transactional
	public UserFavoriteDrinkDetail save(UserFavoriteDrinkDetail fav) {
		
		return favRepo.saveAndFlush(fav);
	}

	@Override
	@Transactional
	public boolean deleteFavorite(Map param) {
		
		List<Long> arr = (List<Long>) param.get("favoriteIdList");
		
		try {
				 favRepo.deleteAllByIdInQuery(arr);
				 return true;
			
			}catch(Exception e) {
				return false;
			}
		
		
	}

	@Override
	@Transactional
	public List<UserFavoriteDTO> selectList(User user) {
		
		//찜목록 조회
		List<UserFavoriteDrinkDetail> list = favRepo.findAllByUser(user);
		
		//QueryDSL
		JPAQueryFactory factory = new JPAQueryFactory(em);
		
		List<UserFavoriteDTO> view = new ArrayList();
		
		for(int i=0;i<list.size();i++) {
			UserFavoriteDrinkDetail fav = list.get(i);
			long id=fav.getDetail().getId();
			DrinkDetail drinkDetail = drinkDetailService.findById((int)id);
			Drink drink = drinkService.selectDrink(drinkDetail.getDrink().getId());
					
			
			UserFavoriteDTO data = UserFavoriteDTO.builder()
													.address(drinkDetail.getStore().getBasicAddress() +" "+ drinkDetail.getStore().getDetailAddress())
													.buyDate(drinkDetail.getBuyDate())
													.favId(id)
													.drinkName(drink.getDrinkName())
													.drinkYear(drinkDetail.getAge())
													.drinkVolumn(drinkDetail.getVolume())
													.cost(drinkDetail.getCost())
													.drinkDetailId(drinkDetail.getId())
													.nickName(drinkDetail.getUser().getNickName())
													.storeName(drinkDetail.getStore().getStoreName())
													.build();
			view.add(data);
		}
		
		
		
		return view;
	}

}
