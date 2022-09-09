package com.barlink.api.admin.controller;

import com.barlink.api.drink.service.DrinkCategoryService;
import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.drink.service.DrinkService;
import com.barlink.api.exception.RestException;
import com.barlink.api.user.service.UserInfoService;
import com.barlink.domain.user.User;
import com.barlink.dto.drink.*;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import com.barlink.dto.drink.category.DrinkCategoryRegisterDto;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/drink")
@RequiredArgsConstructor
public class AdminDrinkController {

    private final DrinkService drinkService;

    private final DrinkCategoryService drinkCategoryService;

    private final UserInfoService userInfoService;

    private final DrinkDetailService drinkDetailService;

    @GetMapping("/category")
    @ApiOperation(value="get category infos", tags="관리자 - 카테고리 관리 페이지 GET API", notes = "카테고리 관련 데이터 GET")
    @ApiResponses({
            @ApiResponse(code = 200, message = "get category info success"),
            @ApiResponse(code = 500, message = "category is null"),
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminCategoryInfos(HttpServletRequest request, HttpServletResponse response) throws RestException {
        Map result = new HashMap();
        List<DrinkCategoryDto> drinkCategoryDtos = drinkCategoryService.selectAllDrinkCategoryList();
        result.put("categories", drinkCategoryDtos);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/category")
    @ApiOperation(value="Post category infos", tags="관리자 - 카테고리 관리 페이지 POST API", notes = "카테고리 관련 데이터 POST")
    @ApiImplicitParams({	//요청 파라미터
            @ApiImplicitParam(name="body",value="{\"parentCategory\":\"위스키\", \"childCategory\":\"글렘피딕\"}", required=true, dataType = "string", paramType="body"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "category insert success"),
            @ApiResponse(code = 409, message = "category duplicate"),
            @ApiResponse(code = 500, message = "parentCategory or childCategory is null")
    })
    public ResponseEntity<Map<String, Object>> postAdminCategoryInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody DrinkCategoryRegisterDto drinkCategoryRegisterDto) throws RestException {
        Map result = new HashMap();
        drinkCategoryService.insertCategory(drinkCategoryRegisterDto);
        result.put("message", "success");
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @GetMapping("/info")
    @ApiOperation(value="get drink infos", tags="관리자 - 술 정보 관리 페이지 GET API", notes = "해당 페이지에서 필요한 데이터들 리턴(drinkInfos 키가 제일 최상임)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "get drink infos success"),
            @ApiResponse(code = 500, message = "서버 에러 로그 확인")
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminDrinkInfos(HttpServletRequest request, HttpServletResponse response) throws RestException {
        // admin 인지 체크 해줘야할 것 같음
        // 주종, 분류, 상품, 숙성연도 가져와야함
        Map result = new HashMap();
        // 모든 술 정보 가져옴
        AdminDrinkResponseDto adminDrinkResponseDto = drinkService.selectAdminDrinkInfos();
        result.put("drinkInfos", adminDrinkResponseDto);
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/info")
    @ApiOperation(value="post infos", tags="관리자 - 술 정보 관리 페이지 POST API", notes = "input(주종, 분류, 상품명, 연식, 설명)을 받아 drink 테이블에 save")
    @ApiResponses({
            @ApiResponse(code = 200, message = "등록한 술 정보, message"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    public ResponseEntity<Map<String, Object>> registerAdminDrinkInfo(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody DrinkAdminRegisterDto drinkAdminRegisterDto) {
        Map result = new HashMap();
        drinkService.insertOrUpdateDrinkInfo(drinkAdminRegisterDto);
        result.put("message", "success");
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @PostMapping(value="/detail/register")
    @ApiOperation(value="drink_detail_register", tags="어드민 술 상세 구매 정보 등록")
    @ApiResponses({
            @ApiResponse(code = 200, message = "등록한 술 정보, message"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerAdminDrinkDetail(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody DrinkDetailRegisterDto drinkDetailRegisterDto) throws RestException {
        User user = new User();

        if(response.getStatus() == 200) {
            Map userInfoMap = userInfoService.parseingUserInfo(request.getHeader("Authorization").toString());
            user = userInfoService.selectUserInfo(userInfoMap.get("email").toString());
        }
        drinkDetailService.insertDrinkDetailInfo(drinkDetailRegisterDto, user);

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @GetMapping(value="/detail/infos")
    @ApiOperation(value="drink_detail_infos", tags="어드민 술 상세 관리")
    @ApiResponses({
            @ApiResponse(code = 200, message = "슬 상세 정보 담아서 리턴"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminDrinkDetailInfos(HttpServletRequest request, HttpServletResponse response) throws RestException {
        Map result = new HashMap();
        List<AdminDrinkDetailInfoResponseDto> drinkDetailInfos = drinkDetailService.selectDrinkDetailInfosAll();
        result.put("drink_detail_infos", drinkDetailInfos);
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @PatchMapping(value="/detail/infos")
    @ApiOperation(value="drink_detail_infos Update", tags="어드민 술 상세 관리 -> 수정(구현중입니다. 중복체크 기능 미완료)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "슬 상세 정보 담아서 리턴"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    public ResponseEntity<Map<String, Object>> patchAdminDrinkDetailInofs(HttpServletRequest request, HttpServletResponse response, @Valid @RequestBody AdminDrinkDetailInfoRequestDto adminDrinkDetailInfoRequestDto) throws RestException {
        Map result = new HashMap();
        drinkDetailService.updateDrinkDetailInfo(adminDrinkDetailInfoRequestDto);
        result.put("message", "update success");
        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }

    @DeleteMapping(value="/detail/infos")
    @ApiOperation(value="drink_detail_infos Delete", tags="어드민 술 상세 관리 -> 삭제(구현중입니다. 다수의 id 전달 받을시 성능문제)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "삭제된 개수 리턴"),
            @ApiResponse(code = 500, message = "서버 에러 -> 로그 확인"),
    })
    @ApiKeyAuthDefinition(key = "Authorization",description = "accessToken", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "")
    public ResponseEntity<Map<String, Object>> deleteAdminDrinkDetailInofs(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "ids") List<Long> ids) {
        Map result = new HashMap();
        int count = drinkDetailService.deleteDrinkDetailInfoByDrinkDetailId(ids);
        result.put("count", count);

        return new ResponseEntity<Map<String,Object>>(result, HttpStatus.OK);
    }
}
