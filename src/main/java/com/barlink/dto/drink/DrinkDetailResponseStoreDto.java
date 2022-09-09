package com.barlink.dto.drink;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkDetailResponseStoreDto {
    private Long storeId;
    private Long drinkDetailId;
    private String nickName;
    private String storeName;
    private String basicAddress;
    private String detailAddress;
    private String callNumber;
    private Integer cost;
    private String buyDate;
}
