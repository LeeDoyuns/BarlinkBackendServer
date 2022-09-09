package com.barlink.api.store.impl;

import com.barlink.api.store.StoreRegionRepository;
import com.barlink.api.store.StoreRegionService;
import com.barlink.domain.store.StoreRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreRegionServiceImpl implements StoreRegionService {
    private final StoreRegionRepository storeRegionRepository;


    @Override
    public StoreRegion chackAndInsertStoreRegion(String regionName, Long regionCode) {
        // 전달받은 regionCode 앞 두자리 가져오기
        Long regionParentCode = Long.parseLong(regionCode.toString().substring(0, 2));
        // 파싱한 앞 두자리 isExistParentRegionCode로 검증

        StoreRegion parentStoreRegion = new StoreRegion();

        if(!isExistParentRegionCode(regionParentCode)) {
            parentStoreRegion = StoreRegion.builder()
                    .regionName(regionName)
                    .regionCode(regionParentCode)
                    .build();
            storeRegionRepository.save(parentStoreRegion);
        } else {
            parentStoreRegion = storeRegionRepository.findByRegionCode(regionParentCode);
        }

        StoreRegion childStoreRegion = StoreRegion.builder()
                .regionName(regionName)
                .regionCode(regionCode)
                .parent(parentStoreRegion)
                .build();
        storeRegionRepository.save(childStoreRegion);

        return childStoreRegion;
    }

    @Override
    public boolean isExistParentRegionCode(Long storeRegionCode) {
        StoreRegion parentStoreRegion = storeRegionRepository.findByRegionCode(storeRegionCode);
        if(parentStoreRegion != null) {
            return true;
        } else {
            return false;
        }
    }
}
