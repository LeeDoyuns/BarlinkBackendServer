package com.barlink.api.store;

import com.barlink.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Object> {
    Store findById(Long storeId);
}
