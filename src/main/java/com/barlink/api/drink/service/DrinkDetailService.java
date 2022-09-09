package com.barlink.api.drink.service;

import com.barlink.api.exception.RestException;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.user.User;
import com.barlink.dto.drink.*;
import org.json.simple.JSONObject;

import java.util.List;

public interface DrinkDetailService {
    DrinkDetail insertDrinkDetailInfo(DrinkDetailRegisterDto drinkDetailRegisterDto, User user) throws RestException;

    JSONObject selectNewDrinkDetailList();

    DrinkDetailResponseDto selectDrinkDetailInfos(DrinkDetailRequestDto drinkDetailRequestDto, String userId) throws RestException;

    DrinkDetail updateDrinkDetailInfo(AdminDrinkDetailInfoRequestDto adminDrinkDetailInfoRequestDto) throws RestException;

    List<AdminDrinkDetailInfoResponseDto> selectDrinkDetailInfosAll() throws RestException;

    DrinkDetail findById(int boardId);

    int deleteDrinkDetailInfoByDrinkDetailId(List<Long> ids);

	List<DrinkDetail> findAllById(List<Long> list);
}
