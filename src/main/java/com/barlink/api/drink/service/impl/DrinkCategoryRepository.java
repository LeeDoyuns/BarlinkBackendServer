package com.barlink.api.drink.service.impl;

import com.barlink.domain.drink.DrinkCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DrinkCategoryRepository extends JpaRepository<DrinkCategory, Object> {

    @Query(value = "select dc from DrinkCategory dc where dc.parent IS NULL")
    List<DrinkCategory> selectParentCategory();


    @Query(value = "select dc from DrinkCategory dc where dc.categoryName =:parentCategoryName")
    DrinkCategory findByParentCategoryName(@Param("parentCategoryName") String parentCategoryName);

    @Query(value = "select dc from DrinkCategory dc where dc.categoryName =:childCategoryName")
    DrinkCategory findByChildCategoryName(@Param("childCategoryName")String childCategoryName);

    @Query(value = "select dc from DrinkCategory dc where dc.categoryName =:parentCategory and dc.parent IS NULL")
    DrinkCategory duplicateParentCategoryCheck(@Param("parentCategory") String parentCategory);

    @Query(value = "select dc from DrinkCategory dc where dc.categoryName =:childCategory and dc.parent IS NOT NULL")
    DrinkCategory duplicateChildCategoryCheck(@Param("childCategory") String childCategory);
}
