package com.barlink.api.drink.service.impl;

import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkYearRepository extends JpaRepository<DrinkYear, Object> {

    @Query(value="select dy from DrinkYear dy where dy.drinkYear.id =:id")
    List<DrinkYear> selectDrinkYearByDrinkId(@Param("id") Long drinkId);


    DrinkYear findByAgeAndDrinkYear(String age, Drink drink);
}
