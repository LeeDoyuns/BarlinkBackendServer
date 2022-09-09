package com.barlink.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    private String createdBy;

    private LocalDateTime createdDate;

    private String updatedBy;

    private LocalDateTime updatedDate;

    public BaseEntity() {
        this.createdBy = "barlink";
        this.createdDate = LocalDateTime.now();
        this.updatedBy = "barlink";
        this.updatedDate = LocalDateTime.now();
    }

    public BaseEntity(String createdBy, LocalDateTime createdDate, String updatedBy, LocalDateTime updatedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
    }
}
