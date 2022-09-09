package com.barlink.api.user.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barlink.domain.user.User;
import com.barlink.domain.user.UserFavoriteDrinkDetail;

/**
 * InfoRepository
 * @author LeeDoyun
 *
 */
public interface UserFavoriteDrinkDetailRepository  extends JpaRepository<UserFavoriteDrinkDetail,Object>{

	@Transactional
	@Modifying(clearAutomatically = true,flushAutomatically = true)
	@Query(value="delete from UserFavoriteDrinkDetail uf where uf.favId in :arr ")
	void deleteAllByIdInQuery(@Param("arr") List<Long> arr);

	List<UserFavoriteDrinkDetail> findAllByUser(User user);

	@Query(value="select uf from UserFavoriteDrinkDetail uf where uf.user = :userId")
	List<UserFavoriteDrinkDetail> findAllByUserId(Long userId);

	@Query(value="select uf.detail from UserFavoriteDrinkDetail uf where uf.user = :user")
	List<Long> findAllDrinkIdByUserId(User user);

	
}
