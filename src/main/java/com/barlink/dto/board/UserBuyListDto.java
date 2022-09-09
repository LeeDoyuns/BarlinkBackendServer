package com.barlink.dto.board;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(description = "DrinkUserBuyListDto Model")
@Getter
@Setter
@NoArgsConstructor
public class UserBuyListDto {
    private Long drinkDetailId;
    private String drinkName;
    private String age;
    private Integer volume;
    private Integer cost;
    private LocalDateTime createdDate;
    private Character isAccess;
//    private Long storeId;
    private String storeName;
    private String basicAddress;
    private String detailAddress;
    private String callNumber;
}
