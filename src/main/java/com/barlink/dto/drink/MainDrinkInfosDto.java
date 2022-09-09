package com.barlink.dto.drink;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainDrinkInfosDto {
    private Long id;

    private String drinkName;

    private String age;

    private Integer costMin;

    private Integer costMax;

    private Long categoryId;
}
