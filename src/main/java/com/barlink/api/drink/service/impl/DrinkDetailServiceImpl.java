package com.barlink.api.drink.service.impl;

import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.exception.RestException;
import com.barlink.api.store.StoreRegionService;
import com.barlink.api.store.StoreRepository;
import com.barlink.api.store.StoreService;
import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkCategory;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.store.Store;
import com.barlink.domain.store.StoreRegion;
import com.barlink.domain.user.User;
import com.barlink.dto.drink.*;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrinkDetailServiceImpl implements DrinkDetailService {

    static Logger log = LoggerFactory.getLogger(DrinkDetailServiceImpl.class);

    private static final String drinkNotName = "여기에 없어요!";

    private final DrinkCategoryRepository drinkCategoryRepository;

    private final DrinkRepository drinkRepository;

    private final StoreRepository storeRepository;

    private final StoreService storeService;

    private final StoreRegionService storeRegionService;

    private final DrinkDetailRepository drinkDetailRepository;

    @Override
    @Transactional
    public DrinkDetail insertDrinkDetailInfo(DrinkDetailRegisterDto drinkDetailRegisterDto, User user) throws RestException {

        DrinkDetail drinkDetail = new DrinkDetail();

        String drinkName = drinkDetailRegisterDto.getDrinkName();
        Drink drink = this.drinkNameCheck(drinkName);

        // Store Setting
        Long storeId = drinkDetailRegisterDto.getStoreId();
        Store store = new Store();
        StoreRegion storeRegion = new StoreRegion();

        // store가 없는경우 따로 처리
        // store, storeRegion 저장코드 분리 필요
        if((storeId == 0 || storeId == null)) {
            store.setStoreName(drinkDetailRegisterDto.getStoreName());
            store.setBasicAddress(drinkDetailRegisterDto.getBasicAddress());
            store.setDetailAddress(drinkDetailRegisterDto.getDetailAddress());
            store.setRegionCode(drinkDetailRegisterDto.getRegionCode());
            store.setLatitude(drinkDetailRegisterDto.getLatitude());
            store.setLongitude(drinkDetailRegisterDto.getLongitude());
            store.setCallNumber(drinkDetailRegisterDto.getCallNumber());

            // StoreRegion Save
            storeRegion = storeRegionService.chackAndInsertStoreRegion(drinkDetailRegisterDto.getRegionName(), drinkDetailRegisterDto.getRegionCode());

            // Store Save
            storeService.insertStore(store);
        } else {
            // 저장된 스토어가 있는 경우
            store = storeRepository.findById(storeId);
            // DB에 해당 store 못찾으면 에러 발생 로직 추가
            if(store == null) {
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "store is not found", 500);
            }
        }

        drinkDetail = DrinkDetail.createDrinkDetail(drinkDetailRegisterDto, drink, store, storeRegion, user);

        if(drinkDetail == null) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "drinkDetail이 저장하는 과정에서 오류 발생", 500);

        drinkDetailRepository.save(drinkDetail);
        return drinkDetail;
    }

    private Drink drinkNameCheck(String drinkName) throws RestException {
        if(drinkName.equals(drinkNotName)) {
            // DB에 상품명 -> 여기에없어요! 데이터 들어올 시
            return null;
        } else {
            Drink drink = drinkRepository.findByDrinkName(drinkName);
            // DB에 해당 drink 못찾으면 에러 발생 로직 추가
            if(drink == null) {
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "drink not found", 500);
            }
            return drink;
        }
    }

    @Override
    @Transactional
    public JSONObject selectNewDrinkDetailList() {
        JSONObject newDrinkDetailObj = new JSONObject();

        try{
            List<NewDrinkDto> newDrinkDtos = new ArrayList<>();
            List<DrinkDetail> newDrinkDetailList = drinkDetailRepository.selectNewDrinkDetailList(PageRequest.of(0, 10));

            newDrinkDetailList.stream().forEach(drinkDetail -> {
                NewDrinkDto target = NewDrinkDto.builder()
                        .drinkDetailId(drinkDetail.getId())
                        .category(drinkDetail.getDrink().getDrinkCategory().getCategoryName())
                        .drinkName(drinkDetail.getDrink().getDrinkName())
                        .age(drinkDetail.getAge())
                        .volume(drinkDetail.getVolume())
                        .cost(drinkDetail.getCost())
                        .buyDate(drinkDetail.getBuyDate())
                        .build();
                newDrinkDtos.add(target);
            });

               newDrinkDetailObj.put("new_drink_details", newDrinkDtos);
        } catch(Exception e) {
            log.info("error : " + e);
        }
        return newDrinkDetailObj;
    }

    @Override
    @Transactional
    public DrinkDetailResponseDto selectDrinkDetailInfos(DrinkDetailRequestDto drinkDetailRequestDto, String userId) throws RestException {
        int costOrder = drinkDetailRequestDto.getCostOrder();
        Long drinkId = drinkDetailRequestDto.getDrinkId();
        String age = drinkDetailRequestDto.getAge();
        int volume = drinkDetailRequestDto.getVolume();

        DrinkDetailResponseDto drinkDetailResponseDto = new DrinkDetailResponseDto();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String costFromRegdate = "2021-01-01"; // sample data
        String costToRegdate = simpleDateFormat.format(new Date());

        List<DrinkDetail> drinkdetails = new ArrayList<DrinkDetail>();

        if( costOrder == 1 ) {
            // 구매일 최신순
            drinkdetails = drinkDetailRepository.selectDrinkDetailInfosOrderByBuyDate(drinkId, age, volume, costFromRegdate, costToRegdate);
        } else if(costOrder == 2) {
            // 구매가 오름차순
            drinkdetails = drinkDetailRepository.selectDrinkDetailInfosOrderCost(drinkId, age, volume, costFromRegdate, costToRegdate);
        } else {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "CostOrder 잘못 입력 1 또는 2 입력 가능", 500);
        }

        if(drinkdetails.isEmpty()) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "전달받은 파라미터에 대한 주류 상세정보 존재 하지 않음", 500);

        List<DrinkDetailResponseStoreDto> drinkDetailResponseStoreDto = createDrinkDetailResponseStoreDto(drinkdetails);
        drinkDetailResponseDto.setDrinkDetailResponseStoreDto(drinkDetailResponseStoreDto);

        // Drink find
        Drink drink = drinkRepository.findById(drinkId);

        if(drink != null) {
            drinkDetailResponseDto.setDrinkId(drink.getId());
            drinkDetailResponseDto.setDrinkName(drink.getDrinkName());
            drinkDetailResponseDto.setDescription(drink.getDescription());
        } else {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 drinkId에 대한 정보 찾을 수 없음", 500);
        }

        drinkDetailResponseDto.setAge(age);
        drinkDetailResponseDto.setVolume(volume);

        // costAverage 함수
        Integer costAverage = drinkDetailRepository.findDrinkDetailCostAverage(drinkId, age, volume, costFromRegdate, costToRegdate);
        if(costAverage == null) costAverage = 0;

        // costMin 함수
        Integer costMin = drinkDetailRepository.findDrinkDetailCostMin(drinkId, age, volume, costFromRegdate, costToRegdate);
        if(costMin == null) costMin = 0;

        drinkDetailResponseDto.setCostAverage(costAverage);
        drinkDetailResponseDto.setCostMin(costMin);

        // costFromRegdate, costToRegdate
        drinkDetailResponseDto.setCostFromRegdate(costFromRegdate);
        drinkDetailResponseDto.setCostToRegdate(costToRegdate);

        // totalCount
        drinkDetailResponseDto.setTotalCount(drinkdetails.size());

        return drinkDetailResponseDto;
    }

    private List<DrinkDetailResponseStoreDto> createDrinkDetailResponseStoreDto(List<DrinkDetail> drinkdetails) {
        List<DrinkDetailResponseStoreDto> drinkDetailResponseStoreDto = new ArrayList<>();

        drinkdetails.stream().forEach(drinkDetail -> {
            Store store = drinkDetail.getStore();

            if(store == null) try {
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 주류 상세정보에 대한 스토어를 찾을 수 없음.", 500);
            } catch (RestException e) {
                e.printStackTrace();
            }

            DrinkDetailResponseStoreDto target = DrinkDetailResponseStoreDto.builder()
                    .drinkDetailId(drinkDetail.getId())
                    .storeId(store.getId())
                    .storeName(store.getStoreName())
                    .basicAddress(store.getBasicAddress())
                    .detailAddress(store.getDetailAddress())
                    .callNumber(store.getCallNumber())
                    .cost(drinkDetail.getCost())
                    .buyDate(drinkDetail.getBuyDate())
                    .nickName(drinkDetail.getUser().getNickName())
                    .build();

            drinkDetailResponseStoreDto.add(target);
        });
        return drinkDetailResponseStoreDto;
    }

    @Override
    @Transactional
    public List<AdminDrinkDetailInfoResponseDto> selectDrinkDetailInfosAll() throws RestException {
        List<AdminDrinkDetailInfoResponseDto> adminDrinkDetailInfoResponseDto = new ArrayList<>();

        List<DrinkDetail> drinkDetails = drinkDetailRepository.findAll();
        if(drinkDetails == null) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "drinkDetails is not found", 500);


        drinkDetails.stream().forEach(drinkDetail -> {
            AdminDrinkDetailInfoResponseDto target = new AdminDrinkDetailInfoResponseDto();

            target.setDrinkDetailId(drinkDetail.getId());
            target.setParentCategory(drinkDetail.getDrink().getDrinkCategory().getParent().getCategoryName());
            target.setChildCategory(drinkDetail.getDrink().getDrinkCategory().getCategoryName());

            //

            target.setDrinkName(drinkDetail.getDrink().getDrinkName());
            target.setAge(drinkDetail.getAge());
            target.setVolume(drinkDetail.getVolume());
            target.setCost(drinkDetail.getCost());

            target.setStoreName(drinkDetail.getStore().getStoreName());
            target.setBasicAddress(drinkDetail.getStore().getBasicAddress());
            target.setDetailAddress(drinkDetail.getStore().getDetailAddress());
            target.setCallNumber(drinkDetail.getStore().getCallNumber());

            target.setCreatedDate(drinkDetail.getCreatedDate());
            target.setUpdatedDate(drinkDetail.getUpdatedDate());
            target.setIsAccess(drinkDetail.getIsAccess());
            target.setIsDeleted(drinkDetail.getIsDeleted());

            target.setUserNickName(drinkDetail.getUser().getNickName());

            adminDrinkDetailInfoResponseDto.add(target);
        });

        return adminDrinkDetailInfoResponseDto;
    }

    @Override
    @Transactional
    public DrinkDetail updateDrinkDetailInfo(AdminDrinkDetailInfoRequestDto adminDrinkDetailInfoRequestDto) throws RestException {

        Long drinkDetailId = adminDrinkDetailInfoRequestDto.getDrinkDetailId();
        DrinkDetail drinkDetail = drinkDetailRepository.findById(drinkDetailId).get();
        if(drinkDetail == null) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "drinkDetails is not found", 500);

        // 중복체크 해야할듯 -> 변경하려는 childCategory 이미 있을 경우 에러처리
        // ParentCategory도 변경된다면?

        DrinkCategory parentCategory = drinkCategoryRepository.findByParentCategoryName(adminDrinkDetailInfoRequestDto.getParentCategory());
        if(parentCategory != null) throw new RestException(HttpStatus.CONFLICT, "이미 존재하는 주종 카테고리", 409);
//        else drinkDetail.getDrink().getDrinkCategory().setParent(adminDrinkDetailInfoRequestDto.getParentCategory());

        DrinkCategory childCategory = drinkCategoryRepository.findByChildCategoryName(adminDrinkDetailInfoRequestDto.getChildCategory());
        if(childCategory != null) throw new RestException(HttpStatus.CONFLICT, "이미 존재하는 분류 카테고리", 409);
//        else drinkDetail.getDrink().setDrinkCategory(adminDrinkDetailInfoRequestDto.getChildCategory());

        // 중복체크 해야할듯 -> 변경하려는 DrinkName이 이미 있을 경우 에러처리
        Drink drink = drinkRepository.findByDrinkName(drinkDetail.getDrink().getDrinkName());
        if(drink != null) throw new RestException(HttpStatus.CONFLICT, "이미 존재하는 주류 이름", 409);
        else drinkDetail.getDrink().setDrinkName(adminDrinkDetailInfoRequestDto.getDrinkName());

        drinkDetail.setAge(adminDrinkDetailInfoRequestDto.getAge());
        drinkDetail.setVolume(adminDrinkDetailInfoRequestDto.getVolume());
        drinkDetail.setCost(adminDrinkDetailInfoRequestDto.getCost());
        drinkDetail.setIsAccess(adminDrinkDetailInfoRequestDto.getIsAccess());

        Store store = drinkDetail.getStore();
        store.setStoreName(adminDrinkDetailInfoRequestDto.getStoreName());
        store.setBasicAddress(adminDrinkDetailInfoRequestDto.getBasicAddress());
        store.setDetailAddress(adminDrinkDetailInfoRequestDto.getDetailAddress());
        store.setCallNumber(adminDrinkDetailInfoRequestDto.getCallNumber());

        return drinkDetail;
    }

    @Override
    @Transactional
    public int deleteDrinkDetailInfoByDrinkDetailId(List<Long> ids) {
        int count = 0;

        // 전달받은 ids값 중 drink_detail에 없는 id라면?
        count = drinkDetailRepository.deleteDrinkDetailInfoByIds(ids);

        return count;
    }

    /**
     * DrinkDetail 정보 조회 - 찜 하기 기능
     * 2021.07.11 LeeDoyun
     */
	@Override
	public DrinkDetail findById(int boardId) {
		// TODO Auto-generated method stub
		return drinkDetailRepository.findById((long)boardId).get();
	}

	@Override
	public List<DrinkDetail> findAllById(List<Long> list) {
		return drinkDetailRepository.findAllById(list);
	}
}
