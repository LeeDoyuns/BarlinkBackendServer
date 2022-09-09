package com.barlink.api.store;

import com.barlink.domain.store.StoreRegion;

public interface StoreRegionService {
    StoreRegion chackAndInsertStoreRegion(String regionName, Long regionCode);
    boolean isExistParentRegionCode(Long regionCode);
}
