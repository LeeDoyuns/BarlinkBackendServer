package com.barlink.api.drink.service;

import com.barlink.api.exception.RestException;
import com.barlink.domain.drink.DrinkCategory;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import com.barlink.dto.drink.category.DrinkCategoryRegisterDto;

import java.util.List;

public interface DrinkCategoryService {

    List<DrinkCategoryDto> selectAllDrinkCategoryList() throws RestException;

    List<DrinkCategory> selectParentCategory();

    void insertCategory(DrinkCategoryRegisterDto drinkCategoryRegisterDto) throws RestException;
}
