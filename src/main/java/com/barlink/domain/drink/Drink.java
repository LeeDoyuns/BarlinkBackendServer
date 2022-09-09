package com.barlink.domain.drink;

import com.barlink.domain.BaseEntity;
import com.barlink.domain.file.File;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drink extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "DRINK_ID")
    private Long id;

    @Column(name="DRINK_NAME", nullable = false, unique=true)
    private String drinkName;

    private String description;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "DRINK_CATEGORY_ID")
    private DrinkCategory drinkCategory;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "FILE_ID")
    private File file;

    @OneToMany(mappedBy = "drinkVolume", targetEntity = Volume.class)
    private List<Volume> volumes = new ArrayList<>();

    @OneToMany(mappedBy = "drinkYear", targetEntity = DrinkYear.class)
    private List<DrinkYear> drinkYears = new ArrayList<>();

    @OneToMany(mappedBy = "drink", targetEntity = DrinkDetail.class, cascade = { CascadeType.REMOVE })
    private List<DrinkDetail> drinkDetails = new ArrayList<>();
}
