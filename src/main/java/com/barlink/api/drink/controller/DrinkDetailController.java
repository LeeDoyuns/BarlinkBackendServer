package com.barlink.api.drink.controller;

import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.exception.RestException;
import com.barlink.api.user.service.UserInfoService;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.domain.user.User;
import com.barlink.dto.drink.DrinkDetailRegisterDto;
import com.barlink.dto.drink.DrinkDetailRequestDto;
import com.barlink.dto.drink.DrinkDetailResponseDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/drink/detail")
@RequiredArgsConstructor
public class DrinkDetailController {

    static Logger log = LoggerFactory.getLogger(DrinkDetailController.class);

    private final CommonRequestBodyWrapper commonWrapper;

    private final UserInfoService userInfoService;

    private final DrinkDetailService drinkDetailService;

    @PostMapping(value="/register")
    @ApiOperation(value="drink_register", tags="주류 구매정보 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "등록한 술 정보, message"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
            @ApiResponse(code = 403,message = "accessToken으로 조회되지 않는 유저."),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerDrink(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody DrinkDetailRegisterDto drinkDetailRegisterDto) throws RestException {
        User user = new User();
        Map<String,Object> result = new HashMap<String,Object>();
        int resCode = response.getStatus();

        // login user check
        if(resCode == 200) {
            Map userInfoMap = userInfoService.parseingUserInfo(request.getHeader("Authorization").toString());
            user = userInfoService.selectUserInfo(userInfoMap.get("email").toString());

            if (user.getUserId() == null) {
                response.setStatus(response.SC_FORBIDDEN); // 403 error
            }

            drinkDetailService.insertDrinkDetailInfo(drinkDetailRegisterDto, user);
            result.put("message", "insert success");
        }

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/infos")
    @ApiOperation(value="drink_detail_infos", tags="주류클릭 시(주류 상세 정보들) - /api/drink/detail/infos", notes="costOrder 1 : 구매일 최신순 , costOrder 2 : 구매가 오름차순")
    @ApiResponses({
            @ApiResponse(code = 200, message = "success"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
            @ApiResponse(code = 403,message = "accessToken으로 조회되지 않는 유저."),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDrinkDetailInfos(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody DrinkDetailRequestDto drinkDetailRequestDto) throws RestException {
        // costOrder 1 : 구매일 최신순 , costOrder 2 : 구매가 오름차순
        // 전달받은 id(주종 : drinkId, age, volume)로 주류 상세 정보 drink_infos_detail -> 주종명, 연식, 주류설명, 용량, 평균가, 최저가, 가격 산정 기간
        // default 30개, 무한스크롤링

        Map<String,Object> result = new HashMap<String,Object>();
        ResponseEntity<Map<String, Object>> res = null;
        int resCode = response.getStatus();

        if(resCode == 200) {
            Map userInfoMap = userInfoService.parseingUserInfo(request.getHeader("Authorization").toString());
            String userId = userInfoMap.get("email").toString();

            if(userId == null) {
                response.setStatus(response.SC_FORBIDDEN); // 403 error
            }

            DrinkDetailResponseDto drinkDetailResponseDto = drinkDetailService.selectDrinkDetailInfos(drinkDetailRequestDto, userId);
            result.put("drink_detail_info", drinkDetailResponseDto);
            response.setStatus(response.SC_OK);

            res = new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
        }
        else {
            //203반환
            response.setStatus(response.SC_FORBIDDEN);
        }

        return res;
    }
}
