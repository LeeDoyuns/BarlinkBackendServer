package com.barlink.dto.drink;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(description = "AdminDrinkDetailInfoResponse Model")
@Getter
@Setter
public class AdminDrinkDetailInfoResponseDto {
    Long drinkDetailId;
    String parentCategory;
    String childCategory;
    String drinkName;
    String age;
    int volume;
    int cost;
    String storeName;
    String basicAddress;
    String detailAddress;
    String callNumber;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    Character isAccess;
    Character isDeleted;
    String userNickName;
}
