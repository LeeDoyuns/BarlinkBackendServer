package com.barlink.dto.drink;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewDrinkDto {
    private Long drinkDetailId;

    private String category;

    private String drinkName;

    private String age;

    private Integer volume;

    private Integer cost;

    private String buyDate;
}
