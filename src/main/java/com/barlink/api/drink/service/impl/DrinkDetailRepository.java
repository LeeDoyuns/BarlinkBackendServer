package com.barlink.api.drink.service.impl;

import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.user.User;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrinkDetailRepository extends JpaRepository<DrinkDetail, Object> {

    @Query(value = "select dt from DrinkDetail dt ORDER BY dt.buyDate desc, dt.cost asc")
    List<DrinkDetail> selectNewDrinkDetailList(PageRequest of);

    @Query(value = "select min(dt.cost) FROM DrinkDetail dt where dt.age =:age")
    Integer getMinCostByAge(String age);

    @Query(value = "select max(dt.cost) FROM DrinkDetail dt where dt.age =:age")
    Integer getMaxCostByAge(String age);

    @Query(value = "select dt from DrinkDetail dt where dt.drink.id =:drinkId and dt.age =:age and dt.volume =:volume and dt.isAccess ='Y' and dt.isDeleted = 'N' and dt.isHide = 'N' and dt.buyDate >=:costFromRegdate and dt.buyDate <=:costToRegdate order by dt.buyDate")
    List<DrinkDetail> selectDrinkDetailInfosOrderByBuyDate(@Param("drinkId") Long drinkId, @Param("age") String age, @Param("volume") int volume, @Param("costFromRegdate") String costFromRegdate, @Param("costToRegdate") String costToRegdate);

    @Query(value = "select dt from DrinkDetail dt where dt.drink.id =:drinkId and dt.age =:age and dt.volume =:volume and dt.isAccess ='Y' and dt.isDeleted = 'N' and dt.isHide = 'N' and dt.buyDate >=:costFromRegdate and dt.buyDate <=:costToRegdate order by dt.cost")
    List<DrinkDetail> selectDrinkDetailInfosOrderCost(@Param("drinkId") Long drinkId, @Param("age") String age, @Param("volume") int volume, @Param("costFromRegdate") String costFromRegdate, @Param("costToRegdate") String costToRegdate);

    @Query(value = "select AVG(dt.cost) from DrinkDetail dt where dt.drink.id =:drinkId and dt.age =:age and dt.volume =:volume and dt.isAccess ='Y' and dt.isDeleted = 'N' and dt.isHide = 'N' and dt.buyDate >=:costFromRegdate and dt.buyDate <=:costToRegdate")
    Integer findDrinkDetailCostAverage(@Param("drinkId") Long drinkId, @Param("age") String age, @Param("volume") int volume, @Param("costFromRegdate") String costFromRegdate, @Param("costToRegdate") String costToRegdate);

    @Query(value = "select MIN(dt.cost) from DrinkDetail dt where dt.drink.id =:drinkId and dt.age =:age and dt.volume =:volume and dt.isAccess ='Y' and dt.isDeleted = 'N' and dt.isHide = 'N' and dt.buyDate >=:costFromRegdate and dt.buyDate <=:costToRegdate")
    Integer findDrinkDetailCostMin(@Param("drinkId") Long drinkId, @Param("age") String age, @Param("volume") int volume, @Param("costFromRegdate") String costFromRegdate, @Param("costToRegdate") String costToRegdate);

    @Query(value = "delete from DrinkDetail dc where dc.id in :ids")
    @Modifying
    int deleteDrinkDetailInfoByIds(@Param("ids") List<Long> ids);

    @Query(value="select dc from DrinkDetail dc where dc.id in list")
    List<DrinkDetail> findAllById(List<Long> list);

	List<DrinkDetail> findAllByUser(User user);
}
