package com.barlink.domain.store;

import com.barlink.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRegion extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "STORE_REGION_ID")
    private Long id;

    private String regionName;

    private Long regionCode;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PARENT_ID")
    private StoreRegion parent;

    @OneToMany(mappedBy = "parent")
    private List<StoreRegion> child = new ArrayList<>();

    @OneToMany(mappedBy = "storeRegion", targetEntity = Store.class)
    private List<Store> stores = new ArrayList<>();
}
