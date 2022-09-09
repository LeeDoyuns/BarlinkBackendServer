package com.barlink.api.drink.service;

import com.barlink.api.exception.RestException;
import com.barlink.domain.drink.Drink;
import com.barlink.dto.drink.AdminDrinkResponseDto;
import com.barlink.dto.drink.DrinkAdminRegisterDto;
import com.barlink.dto.drink.DrinkNamesDto;
import com.barlink.dto.drink.MainDrinkInfosDto;
import org.json.simple.JSONObject;

import java.util.List;

public interface DrinkService {
    List<DrinkNamesDto> selectDrinkNamesByCatregoryId(Long categoryId) throws RestException;

    List<MainDrinkInfosDto> selectDrinkInfosByDrinkId(Long drinkId) throws RestException;

    JSONObject selectDrinkInfos();

    AdminDrinkResponseDto selectAdminDrinkInfos() throws RestException;

    List<Drink> selectDrinkNames();

    void insertOrUpdateDrinkInfo(DrinkAdminRegisterDto drinkAdminRegisterDto);

	Drink selectDrink(long drinkId);
}
