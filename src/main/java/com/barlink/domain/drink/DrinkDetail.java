package com.barlink.domain.drink;

import com.barlink.domain.BaseEntity;
import com.barlink.domain.store.Store;
import com.barlink.domain.store.StoreRegion;
import com.barlink.domain.user.User;

import com.barlink.dto.drink.DrinkDetailRegisterDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

import java.util.List;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrinkDetail extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DRINK_DETAIL_ID")
    private Long id;

    private String note;

    private String buyDate;

    private Character isDeleted;

    private Character isAccess;

    private Character isHide;

    private String createProgramId;

    private Integer cost;

    private Integer volume;

    private String age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DRINK_ID")
    public Drink drink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID")
    private Store store;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    public void setStore(Store store, StoreRegion storeRegion) {
        this.store = store;
        store.setStoreRegion(storeRegion);
        store.getDrinkDetails().add(this);
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
        drink.getDrinkDetails().add(this);
    }

    //== 생성 메서드==//
    public static DrinkDetail createDrinkDetail(DrinkDetailRegisterDto drinkDetailRegisterDto, Drink drink, Store store, StoreRegion storeRegion, User user) {
        DrinkDetail drinkDetail = new DrinkDetail();

        // Drink Setting
        drinkDetail.setDrink(drink);

        // Store Setting
        drinkDetail.setStore(store, storeRegion);

        // isAccess Column Setting
        if(user.getUserRole().equals("ADMIN")) {
            drinkDetail.setIsAccess('Y');
        } else {
            drinkDetail.setIsAccess('N');
        }

        // 나머지 컬럼들 셋팅
        drinkDetail.setNote(drinkDetailRegisterDto.getNote());
        drinkDetail.setBuyDate(drinkDetailRegisterDto.getBuyDate());
        drinkDetail.setIsDeleted('N');
        drinkDetail.setIsHide('N');
        drinkDetail.setCreateProgramId(drinkDetailRegisterDto.getCreateProgramId());
        drinkDetail.setCost(drinkDetailRegisterDto.getCost());
        drinkDetail.setVolume(drinkDetailRegisterDto.getVolume());
        drinkDetail.setAge(drinkDetailRegisterDto.getAge());
        drinkDetail.setUser(user);

        return drinkDetail;
    }
}
