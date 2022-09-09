package com.barlink.dto.board;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBuyListStoreDto {
    private Long storeId;
    private String StoreName;
    private String basicAddress;
    private String detailAddress;
    private String callNumber;
}
