package com.barlink.api.drink.controller;

import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.drink.service.DrinkService;
import com.barlink.api.exception.RestException;
import com.barlink.dto.drink.DrinkNamesDto;
import com.barlink.dto.drink.MainDrinkInfosDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/drink")
@RequiredArgsConstructor
public class DrinkController {

	static Logger log = LoggerFactory.getLogger(DrinkController.class);

	private final DrinkService drinkService;

	private final DrinkDetailService drinkDetailService;

	@GetMapping(value="/names/{categoryId}")
	@ApiOperation(value="drink_names", tags="메인페이지(카테고리ID 받아 등록된 술 상품명들 리턴) - /api/drink/names/{categoryId}")
	@ApiImplicitParams({
			@ApiImplicitParam(name="categoryId", value="카테고리 id", required=true, dataType = "int"),
	})
	@ApiResponses({
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "해당 카테고리 Id로 찾을 수 있는 주류존재 하지 않음"),
	})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDrinkNamesByCategoryId(@PathVariable("categoryId") Long categoryId) throws RestException {
		// 카테고리(분류)에 따른 주류 상품들 drink_names
		// category_id로 drink테이블 조회해 주는 쿼리작성
		// 해당 데이터들 파싱해서 json으로 보내주면 될듯?
		Map<String,Object> result = new HashMap<String,Object>();
		List<DrinkNamesDto> drinkNamesDtos = drinkService.selectDrinkNamesByCatregoryId(categoryId);
		result.put("drink_names", drinkNamesDtos);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}

	@GetMapping(value="/names/info/{drinkId}")
	@ApiOperation(value="drink_infos", tags="메인페이지(drink id받아 해당 상품명의 정보들 리턴) - /api/drink/names/info/{drinkId}", notes = "연식(year) 별로 상품명 group by해서 데이터 나옴")
	@ApiImplicitParams({
			@ApiImplicitParam(name="drinkId", value="상품명 Id", required=true, dataType = "int"),
	})
	@ApiResponses({
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "해당 drinkId로 찾을 수 있는 연식이 존재 하지 않음"),
	})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getDrinkInfosByDrinkId(@PathVariable("drinkId") Long drinkId) throws RestException {
		// 주류 상세 정보 drink_infos -> 상품명, 연식, min ~ max가격
		// 주류상세 - 구매정보에서 상품명별로 groupBy(연식) min ~ max 가격
		Map<String,Object> result = new HashMap<String,Object>();
		List<MainDrinkInfosDto> mainDrinkInfosDtos = drinkService.selectDrinkInfosByDrinkId(drinkId);
		result.put("drink_infos", mainDrinkInfosDtos);
		return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
	}

	@GetMapping(value="/new")
	@ApiOperation(value="drink_info_new", tags="메인페이지 - 새로운 술 정보(1순위 구매일 내림차순, 2순위 구매가 오름차순)으로 10개 데이터 리턴")
	@ApiResponses({
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
			@ApiResponse(code = 400, message = "잘못된 요청 문법 오류 확인")
	})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getNewDrinkInfos() {
		// 구매 정보에서 등록된 술 정보 리스트 10개 : new_drink_infos
		// -> 구입일, 분류 카테고리, 상품명 , 연식, 용량, 가격
		// 정렬 기준 : 1순위 구매일 최신순, 2순위 구매가 오름차순
		ResponseEntity<Map<String, Object>> res = null;

		try {
			JSONObject newDrinkDetailList = drinkDetailService.selectNewDrinkDetailList();
			res = new ResponseEntity<Map<String,Object>>(newDrinkDetailList, HttpStatus.OK);
		} catch (Exception e) {
			log.info("error : " + e);
		}

		return res;
	}
}
