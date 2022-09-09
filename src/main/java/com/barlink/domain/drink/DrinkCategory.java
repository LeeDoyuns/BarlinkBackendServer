package com.barlink.domain.drink;

import com.barlink.domain.BaseEntity;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrinkCategory extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "DRINK_CATEGORY_ID")
    private Long id;

    @Column(name="CATEGORY_NAME", nullable = false, unique=true)
    private String categoryName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARENT_ID")
    private DrinkCategory parent;

    @OneToMany(mappedBy = "parent")
    private List<DrinkCategory> child = new ArrayList<>();

    @OneToMany(mappedBy = "drinkCategory", cascade = { CascadeType.REMOVE })
    private List<Drink> drinks = new ArrayList<>();

    public List<DrinkCategory> getChild() {
        return child;
    }

    //== 생성 메서드==/
    public static DrinkCategory createParentDrinkCategory(String parentCategory) {
        DrinkCategory drinkCategory = new DrinkCategory();
        drinkCategory.setCategoryName(parentCategory);
        return drinkCategory;
    }

    public static DrinkCategory createChildDrinkCategory(DrinkCategory drinkParentCategory, String childCategory) {
        DrinkCategory drinkCategory = new DrinkCategory();
        drinkCategory.setCategoryName(childCategory);
        drinkCategory.setParent(drinkParentCategory);
        return drinkCategory;
    }

    //== 비즈니스 로직==/

    //== 조회 로직==/
}
