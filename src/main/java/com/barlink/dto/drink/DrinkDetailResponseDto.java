package com.barlink.dto.drink;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrinkDetailResponseDto {

    private Long drinkId;
    private String drinkName;
    private String age;
    private Integer volume;
    private String description;

    private Integer costAverage;
    private Integer costMin;
    private String costFromRegdate;
    private String costToRegdate;
    private Integer totalCount;

    private List<DrinkDetailResponseStoreDto> drinkDetailResponseStoreDto;
}
