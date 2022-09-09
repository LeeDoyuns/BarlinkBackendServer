package com.barlink.api.store;

import com.barlink.api.store.StoreRepository;
import com.barlink.domain.store.Store;
import com.barlink.domain.store.StoreRegion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class StoreServiceImplTest {

    @Autowired
    StoreService storeService;

    @Autowired
    StoreRegionService storeRegionService;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreRegionRepository storeRegionRepository;

    @Test
    @Transactional
    public void insertStore() throws Exception{
        // given
        Store newStore = createStore();

        // when
        Long storeId = storeService.insertStore(newStore);

        // then
        assertEquals(newStore, storeRepository.findById(storeId));
    }

    @Test
    @Transactional
    public void isExistStore() {
        // given
        // when
        Store store = storeRepository.findById(1L);
        // then
        assertNotNull(store);
    }

    @Test
    @Transactional
    public void isExistParentRegionCode() {
        // given
        StoreRegion parentStoreRegion = createParentStoreRegion();
        storeRegionRepository.save(parentStoreRegion);
        // when
        boolean flag = storeRegionService.isExistParentRegionCode(11L);
        // then
        assertTrue(flag);
    }

    private Store createStore() {
        Store newStore = Store.builder()
                .storeName("성진상회")
                .basicAddress("서울 구로구 디지털로 31길")
                .detailAddress("래미안아파트 108동 1004호")
                .regionCode(11371L)
                .latitude((float) 32.41)
                .longitude((float) 40.21)
                .callNumber("010-2548-6707")
                .build();

        StoreRegion storeRegion = createParentStoreRegion();

        // 상위 regionCode 체크하는 로직 추가필요
        // 전달받은 regionCode에서 subString으로 앞 두자리 시군코드가 있으면 regionCode(하위코드로만) 저장
        // 없다면 시군코드(상위) + 하위 코드 저장
        storeRegionService.chackAndInsertStoreRegion("구로", 11371L);
        newStore.setStoreRegion(storeRegion);
        return newStore;
    }

    private StoreRegion createParentStoreRegion() {
        StoreRegion storeRegion = StoreRegion.builder()
                .regionName("서울")
                .regionCode(11L)
                .build();
        return storeRegion;
    }

    private StoreRegion createChildStoreRegion() {
        StoreRegion storeRegion = StoreRegion.builder()
                .regionName("서울 구로구")
                .regionCode(11851L)
                .build();
        return storeRegion;
    }

    @Test
    public void getParentCode() {
        // given
        Long regionCode = 11681L;
        // when
        Long regionParentCode = Long.parseLong(regionCode.toString().substring(0, 2));
        // then

        assertEquals(Optional.of(regionParentCode), 11L);
    }
}
