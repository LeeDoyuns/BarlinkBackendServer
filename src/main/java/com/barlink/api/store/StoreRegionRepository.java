package com.barlink.api.store;

import com.barlink.domain.store.StoreRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRegionRepository extends JpaRepository<StoreRegion, Object> {

    @Query(value = "select sg from StoreRegion sg where sg.regionCode =:regionCode")
    StoreRegion findByRegionCode(Long regionCode);
}
