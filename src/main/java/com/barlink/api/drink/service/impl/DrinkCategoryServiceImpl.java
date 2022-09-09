package com.barlink.api.drink.service.impl;

import com.barlink.api.drink.service.DrinkCategoryService;
import com.barlink.api.exception.RestException;
import com.barlink.domain.drink.DrinkCategory;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import com.barlink.dto.drink.category.DrinkCategoryRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DrinkCategoryServiceImpl implements DrinkCategoryService {

    private final DrinkCategoryRepository drinkCategoryRepository;

    @Override
    public List<DrinkCategoryDto> selectAllDrinkCategoryList() throws RestException {
        // 1. 한 번의 쿼리 -> category테이블에서 parentCategory(주종)들만 조회
        List<DrinkCategoryDto> drinkCategoryDtos = new ArrayList<>();
        List<DrinkCategory> parentCategories = this.selectParentCategory();

        if(parentCategories == null) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "parent Category 0개 또는 null", 500);

        for (DrinkCategory drinkCategory: parentCategories) {
            DrinkCategoryDto target = new DrinkCategoryDto();
            List<String> childNames = new ArrayList<>();
            target.setCategoryId(drinkCategory.getId());
            // 여기서의 getCatgegoryName은 ? parentDrinkCategory들을 의미
            target.setCategoryName(drinkCategory.getCategoryName());

            // 주종 중에 2차분류(Child) 가 있는경우 모두 add
            if (!drinkCategory.getChild().isEmpty()) {
                // 주종 -> 2차분류
                for (DrinkCategory child : drinkCategory.getChild()) {
                    childNames.add(child.getCategoryName());
                }
            }

            target.setChild(childNames);

            drinkCategoryDtos.add(target);
        }

        return drinkCategoryDtos;
    }

    @Override
    @Transactional
    public List<DrinkCategory> selectParentCategory() {
        List<DrinkCategory> drinkParentCategories;
        drinkParentCategories = drinkCategoryRepository.selectParentCategory();
        return drinkParentCategories;
    }

    @Override
    @Transactional
    public void insertCategory(DrinkCategoryRegisterDto drinkCategoryRegisterDto) throws RestException {

        String parentCategory = drinkCategoryRegisterDto.getParentCategory();
        String childCategory = drinkCategoryRegisterDto.getChildCategory();

        if (parentCategory == null || childCategory == null) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "parentCateogry 또는 childCategory null", 500);
        }

        // 중복 체크
        // 중복될때 에러 처리 어떻게?
        // parentCategory, childCategory 각각 저장되도록 분리해야할듯..
        if (duplicateParentCategoryCheck(parentCategory)) {
            // 주종 카테고리만 셋팅
            DrinkCategory parentDrinkCategory = DrinkCategory.createParentDrinkCategory(parentCategory);
            drinkCategoryRepository.save(parentDrinkCategory);
        }

        if (duplicateChildCategoryCheck(childCategory)) {
            // 분류 카테고리 셋팅
            DrinkCategory drinkParentCategory = drinkCategoryRepository.findByParentCategoryName(parentCategory);
            DrinkCategory childDrinkCategory = DrinkCategory.createChildDrinkCategory(drinkParentCategory, childCategory);
            drinkCategoryRepository.save(childDrinkCategory);
        }
    }

    @Transactional
    public boolean duplicateParentCategoryCheck(String parentCategory) throws RestException{
        DrinkCategory drinkCategory = drinkCategoryRepository.duplicateParentCategoryCheck(parentCategory);
        // 해당 카테고리가 없다면 중복이되지 않았음
        if(drinkCategory == null) return true;

        throw new RestException(HttpStatus.CONFLICT, "parentCategory duplicate", 409);
    }

    @Transactional
    public boolean duplicateChildCategoryCheck(String childCategory) throws RestException {
        DrinkCategory drinkCategory = drinkCategoryRepository.duplicateChildCategoryCheck(childCategory);
        // 해당 카테고리가 없다면 중복이되지 않았음
        if(drinkCategory == null) return true;

        throw new RestException(HttpStatus.CONFLICT, "childCategory duplicate", 409);
    }
}
