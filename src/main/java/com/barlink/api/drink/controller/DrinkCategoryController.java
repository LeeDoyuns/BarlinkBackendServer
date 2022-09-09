package com.barlink.api.drink.controller;

import com.barlink.api.drink.service.DrinkCategoryService;
import com.barlink.api.exception.RestException;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drink")
@RequiredArgsConstructor
public class DrinkCategoryController {

    static Logger log = LoggerFactory.getLogger(DrinkCategoryController.class);

    private final DrinkCategoryService service;

    @GetMapping(value="/categories")
    @ApiOperation(value="categories", tags="모든 카테고리 조회 - /api/drink/categories")
    @ApiResponses({
            @ApiResponse(code = 200, message = "카테고리 조회 성공"),
            @ApiResponse(code = 500, message = "Parent Category 0개 또는 null인 상태"),
    })
    public ResponseEntity<Map<String, Object>> getDrinkCategories() throws RestException {
        Map<String,Object> result = new HashMap<String,Object>();
        List<DrinkCategoryDto> drinkCategoryDtos = service.selectAllDrinkCategoryList();
        result.put("categories", drinkCategoryDtos);

        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }
}
