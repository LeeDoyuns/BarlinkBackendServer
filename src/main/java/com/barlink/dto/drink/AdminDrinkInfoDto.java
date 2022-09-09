package com.barlink.dto.drink;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdminDrinkInfoDto {
    Long drinkId;
    Long categoryId;
    String categoryName;
    String drinkName;
    List<String> ages;
    String description;
}
