package com.barlink.api.drink.service.impl;

import com.barlink.api.drink.service.DrinkCategoryService;
import com.barlink.api.drink.service.DrinkService;
import com.barlink.api.exception.RestException;
import com.barlink.domain.drink.Drink;
import com.barlink.domain.drink.DrinkCategory;
import com.barlink.domain.drink.DrinkYear;
import com.barlink.dto.drink.*;
import com.barlink.dto.drink.category.DrinkCategoryDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrinkServiceImpl implements DrinkService {

    static Logger log = LoggerFactory.getLogger(DrinkServiceImpl.class);

    private final DrinkCategoryService drinkCategoryService;

    private final DrinkCategoryRepository drinkCategoryRepository;

    private final DrinkRepository drinkRepository;

    private final DrinkDetailRepository drinkDetailRepository;

    private final DrinkYearRepository drinkYearRepository;

    @Override
    @Transactional
    public List<DrinkNamesDto> selectDrinkNamesByCatregoryId(Long categoryId) throws RestException {

        List<DrinkNamesDto> drinkNamesDtos = new ArrayList<>();

        List<Drink> drinkNames = drinkRepository.selectDrinkNamesByCategoryId(categoryId);
        if(drinkNames.isEmpty()) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 카테고리 Id로 찾을 수 있는 주류존재 하지 않음", 500);

        drinkNames.stream().forEach(drink-> {
            DrinkNamesDto target = new DrinkNamesDto();
            target.setDrinkId(drink.getId());
            target.setDrinkName(drink.getDrinkName());
            drinkNamesDtos.add(target);
        });

        return drinkNamesDtos;
    }

    @Override
    @Transactional
    public List<MainDrinkInfosDto> selectDrinkInfosByDrinkId(Long drinkId) throws RestException {
        List<MainDrinkInfosDto> mainDrinkInfosDtos = new ArrayList<>();
        // 전달받은 drinkId로 DRINKYEAR 테이블 외래키 조회
        List<DrinkYear> drinkYear = drinkYearRepository.selectDrinkYearByDrinkId(drinkId);
        if(drinkYear.isEmpty()) throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "해당 drinkId로 찾을 수 있는 연식이 존재 하지 않음" , 500);

        drinkYear.stream().forEach(drink -> {
            String age = drink.getAge();
            MainDrinkInfosDto mainDrinkInfoDto = new MainDrinkInfosDto();
            mainDrinkInfoDto.setId(drink.getId());
            mainDrinkInfoDto.setDrinkName(drink.getDrinkYear().getDrinkName());
            mainDrinkInfoDto.setAge(drink.getAge());
            mainDrinkInfoDto.setCategoryId(drink.getDrinkYear().getDrinkCategory().getId());
            // 위에서 뽑은 age별로 drink_detail의 min, max 값 구하는 쿼리
            Integer minCost = drinkDetailRepository.getMinCostByAge(age);
            if(minCost == null) minCost = 0;
            mainDrinkInfoDto.setCostMin(minCost);

            Integer maxCost = drinkDetailRepository.getMaxCostByAge(age);
            if(maxCost == null) maxCost = 0;
            mainDrinkInfoDto.setCostMax(maxCost);

            mainDrinkInfosDtos.add(mainDrinkInfoDto);
        });

        return mainDrinkInfosDtos;
    }

    @Override
    public JSONObject selectDrinkInfos() {

        JSONObject drinkInfosObj = new JSONObject();

        try {
            List<String> drinkNames = new ArrayList();

            List<Drink> drinks = this.selectDrinkNames();

            drinks.stream().forEach(drink -> {
                drinkNames.add(drink.getDrinkName());
            });

            drinkInfosObj.put("drinks", drinkNames);
        } catch(Exception e) {
            log.info("error : " + e);
        }

        return drinkInfosObj;
    }

    @Override
    public List<Drink> selectDrinkNames() {
        List<Drink> drinks = new ArrayList<>();

        try {
            drinks = drinkRepository.selectDrinkNames();
        } catch(Exception e) {
            log.info("error : " + e);
        }
        return drinks;
    }

    @Override
    @Transactional
    public void insertOrUpdateDrinkInfo(DrinkAdminRegisterDto drinkAdminRegisterDto) {

        String drinkName = drinkAdminRegisterDto.getDrinkName();
        String drinkAge = drinkAdminRegisterDto.getAge();

        Drink drink = drinkRepository.findByDrinkName(drinkName);

        String childCategoryName = drinkAdminRegisterDto.getChildCategory();
        DrinkCategory drinkCategory = drinkCategoryRepository.findByChildCategoryName(childCategoryName);

        // insert or update
        if(drink == null) {
            // 새로운 drink, drinkYear insert
            Drink newDrink = new Drink();
            newDrink.setDescription(drinkAdminRegisterDto.getDescription());
            newDrink.setDrinkName(drinkAdminRegisterDto.getDrinkName());
            newDrink.setDrinkCategory(drinkCategory);

            // 새로 만들어지는 Drink 이기때문에 이와 맵핑되는 DrinkYear도 없으므로 바로 save
            DrinkYear newDrinkYear = new DrinkYear();
            newDrinkYear.setAge(drinkAge);
            newDrinkYear.setDrinkYear(newDrink);
            drinkRepository.save(newDrink);
            drinkYearRepository.save(newDrinkYear);
        } else {
            // 기존에 있는 drink update
            drink.setDescription(drinkAdminRegisterDto.getDescription());
            drink.setDrinkName(drinkAdminRegisterDto.getDrinkName());
            drink.setDrinkCategory(drinkCategory);

            // 기존에 있는 drinkYear update
            DrinkYear currentDrinkYear = drinkYearRepository.findByAgeAndDrinkYear(drinkAge, drink);
            if(currentDrinkYear == null) {
                DrinkYear newDrinkYear = new DrinkYear();
                newDrinkYear.setAge(drinkAge);
                newDrinkYear.setDrinkYear(drink);
                drinkYearRepository.save(newDrinkYear);
            } else {
                currentDrinkYear.setAge(drinkAge);
                currentDrinkYear.setDrinkYear(drink);
            }
        }
    }

    @Override
    @Transactional
    public AdminDrinkResponseDto selectAdminDrinkInfos() throws RestException {
        // 카테고리 찾기
        AdminDrinkResponseDto adminDrinkResponseDto = new AdminDrinkResponseDto();

        List<DrinkCategoryDto> categories = drinkCategoryService.selectAllDrinkCategoryList();
        adminDrinkResponseDto.setDrinkCategoryDto(categories);

        // Drink Table 모두 조회
        List<Drink> drinks = drinkRepository.findAll();
        List<AdminDrinkInfoDto> AdminDrinkInfoDtos = createAdminDrinkInfoDto(drinks);

        adminDrinkResponseDto.setAdminDrinkInfoDto(AdminDrinkInfoDtos);

        return adminDrinkResponseDto;
    }

    private List<AdminDrinkInfoDto> createAdminDrinkInfoDto(List<Drink> drinks) {
        List<AdminDrinkInfoDto> AdminDrinkInfoDtos = new ArrayList<>();

        drinks.stream().forEach(drink -> {
            AdminDrinkInfoDto adminDrinkInfoDto = new AdminDrinkInfoDto();
            adminDrinkInfoDto.setDrinkId(drink.getId());
            adminDrinkInfoDto.setCategoryId(drink.getDrinkCategory().getId());
            adminDrinkInfoDto.setCategoryName(drink.getDrinkCategory().getCategoryName());
            adminDrinkInfoDto.setDescription(drink.getDescription());

            // age 찾은후 셋팅
            List<DrinkYear> years = drinkYearRepository.selectDrinkYearByDrinkId(drink.getId());
            List<String> ages= new ArrayList<>();
            years.stream().forEach(year-> {
                ages.add(year.getAge());
            });
            adminDrinkInfoDto.setAges(ages);

            AdminDrinkInfoDtos.add(adminDrinkInfoDto);
        });

        return AdminDrinkInfoDtos;
    }

	@Override
	public Drink selectDrink(long drinkId) {
		return drinkRepository.findById(drinkId);
	}
}
