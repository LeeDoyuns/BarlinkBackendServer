package com.barlink.api.user.service;

import java.util.List;
import java.util.Map;

import com.barlink.domain.user.User;
import com.barlink.domain.user.UserFavoriteDrinkDetail;
import com.barlink.dto.user.UserFavoriteDTO;

public interface UserFavoriteDrinkDetailService {

	UserFavoriteDrinkDetail save(UserFavoriteDrinkDetail fav);

	boolean deleteFavorite(Map param);

	List<UserFavoriteDTO> selectList(User user);
	

}
