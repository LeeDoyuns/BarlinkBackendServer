package com.barlink.dto.drink.category;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@ApiModel(description = "DrinkCategoryDto Model")
@Getter
@Setter
public class DrinkCategoryDto {
    private Long categoryId;
    private String categoryName;
    private List<String> child;
}
