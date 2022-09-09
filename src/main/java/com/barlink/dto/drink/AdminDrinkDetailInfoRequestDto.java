package com.barlink.dto.drink;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "AdminDrinkDetailInfoRequestDto Model")
@Getter
@Setter
public class AdminDrinkDetailInfoRequestDto {
    Long drinkDetailId;

    String parentCategory;
    String childCategory;

    String drinkName;

    String age;
    int volume;
    int cost;
    Character isAccess;

    String storeName;
    String basicAddress;
    String detailAddress;
    String callNumber;

}
