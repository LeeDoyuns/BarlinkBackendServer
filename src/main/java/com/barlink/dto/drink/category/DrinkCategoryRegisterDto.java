package com.barlink.dto.drink.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "DrinkCategoryRegisterDto Model")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DrinkCategoryRegisterDto {
    @ApiModelProperty(notes = "주종", name = "parentCategory", dataType="String", example="위스키")
    private String parentCategory;
    @ApiModelProperty(notes = "2차분류", name = "childCategory", dataType="String", example="싱글몰트")
    private String childCategory;
}
