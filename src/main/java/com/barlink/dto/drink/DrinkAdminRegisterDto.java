package com.barlink.dto.drink;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;


@ApiModel(description = "AdminDrinkRegister Model")
@Getter
public class DrinkAdminRegisterDto {
    @ApiModelProperty(notes = "주종", name = "parentCategory")
    private String parentCategory;
    @ApiModelProperty(notes = "2차분류", name = "childCategory")
    private String childCategory;
    @ApiModelProperty(notes = "상품명", name = "drinkName")
    private String drinkName;
    @ApiModelProperty(notes = "숙성연도", name = "age")
    private String age;
    @ApiModelProperty(notes = "메모", name = "description")
    private String description;
}
