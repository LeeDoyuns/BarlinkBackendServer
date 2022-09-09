package com.barlink.dto.drink;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkNamesDto {
    private Long drinkId;
    private String drinkName;
}
