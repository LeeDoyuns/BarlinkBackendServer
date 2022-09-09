package com.barlink.domain.drink;

import com.barlink.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class DrinkYear extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "DRINK_YEAR_ID")
    private Long id;

    private String age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "DRINK_ID")
    private Drink drinkYear;
}
