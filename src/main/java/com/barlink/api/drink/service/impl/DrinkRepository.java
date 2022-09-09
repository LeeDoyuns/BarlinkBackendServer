package com.barlink.api.drink.service.impl;

import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Object> {

    @Query(value="select d from Drink d where d.drinkCategory.id =:categoryId")
    List<Drink> selectDrinkNamesByCategoryId(@Param("categoryId") Long categoryId);

    Drink findByDrinkName(String drinkName);

    @Query(value="select d from Drink d")
    List<Drink> selectDrinkNames();

    Drink findById(Long drinkId);
}
