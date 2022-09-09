package com.barlink.api.store.impl;

import com.barlink.api.store.StoreRepository;
import com.barlink.api.store.StoreService;
import com.barlink.domain.store.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public Long insertStore(Store newStore) {
        Store store = storeRepository.save(newStore);
        return store.getId();
    }
}
