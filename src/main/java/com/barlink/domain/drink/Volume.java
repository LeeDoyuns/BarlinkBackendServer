package com.barlink.domain.drink;

import com.barlink.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Volume extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "VOLUME_ID")
    private Long id;

    private Integer amount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "DRINK_ID")
    private Drink drinkVolume;
}
