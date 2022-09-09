package com.barlink.domain.store;

import com.barlink.domain.BaseEntity;
import com.barlink.domain.drink.DrinkDetail;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class Store extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "STORE_ID")
    private Long id;

    private String storeName;

    private String basicAddress;

    private String detailAddress;

    private Long regionCode;

    private Float latitude;

    private Float longitude;

    private String callNumber;

    private String callNumber2;

    private String useStatus;

    @OneToMany(mappedBy = "store", targetEntity = DrinkDetail.class, cascade = { CascadeType.REMOVE })
    private List<DrinkDetail> drinkDetails = new ArrayList<>();

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "store_region_id")
    private StoreRegion storeRegion;

}
