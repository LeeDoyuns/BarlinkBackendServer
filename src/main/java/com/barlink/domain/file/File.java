package com.barlink.domain.file;

import com.barlink.domain.BaseEntity;
import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkDetail;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "FILE_ID")
    private Long id;

    private Character fileType;

    private String originFileName;

    private String saveFileName;

    private String saveFilePath;

    private String createProgramId;

    @OneToMany(mappedBy = "file")
    private List<Drink> drinks = new ArrayList<>();
}
