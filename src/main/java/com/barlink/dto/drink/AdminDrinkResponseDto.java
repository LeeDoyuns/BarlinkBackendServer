package com.barlink.dto.drink;

import com.barlink.dto.drink.category.DrinkCategoryDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(description = "AdminDrinkResponseDto Model")
@Getter
@Setter
public class AdminDrinkResponseDto {
    List<DrinkCategoryDto> drinkCategoryDto;
    List<AdminDrinkInfoDto> adminDrinkInfoDto;
}
