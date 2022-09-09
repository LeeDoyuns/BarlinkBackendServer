package com.barlink.dto.drink;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrinkDetailRequestDto {

    @ApiModelProperty(notes = "drink 아이디", name = "drinkId", dataType = "Long", example="1")
    private Long drinkId;
    @ApiModelProperty(notes = "연식", name = "age", dataType = "String", example="\"18\"")
    private String age;
    @ApiModelProperty(notes = "용량", name = "volume", dataType ="Integer", example="700")
    private Integer volume;
    @ApiModelProperty(notes = "구매가순서", name = "costOrder", dataType="Integer", example="1")
    private Integer costOrder;
}
