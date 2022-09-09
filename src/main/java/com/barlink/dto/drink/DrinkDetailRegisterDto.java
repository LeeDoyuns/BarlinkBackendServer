package com.barlink.dto.drink;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "DrinkDetailRegister Model")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DrinkDetailRegisterDto {

        @ApiModelProperty(notes = "주종", name = "parentCategory", dataType="String", example="\"위스키\"")
        private String parentCategory;
        @ApiModelProperty(notes = "2차분류", name = "childCategory", dataType="String", example="\"싱글몰트\"")
        private String childCategory;
        @ApiModelProperty(notes = "상품명", name = "drinkName", dataType="String", example="\"글렌피딕\"")
        private String drinkName;

        @ApiModelProperty(notes = "숙성연도", name = "age", dataType="String", example="\"18\"")
        private String age;
        @ApiModelProperty(notes = "용량", name = "volume", dataType="Integer", example="700")
        private Integer volume;
        @ApiModelProperty(notes = "가격", name = "cost", dataType="Integer", example="150000")
        private Integer cost;
        @ApiModelProperty(notes = "생성일", name = "buyDate", dataType="String", example="\"2021-11-15\"")
        private String buyDate;
        @ApiModelProperty(notes = "메모", name = "note", dataType="String", example="\"글렌피딕에 대한 간략한 메모\"")
        private String note;
        @ApiModelProperty(notes = "프로그램 아이디", name = "createProgramId", dataType="String", example="\"MacBook\"")
        private String createProgramId;

        @ApiModelProperty(notes = "상점 아이디, DB에 없는 상점일시 0값 보내주거나 안보내주면됨", name = "storeId", dataType = "Long", example="1")
        private Long storeId;
        @ApiModelProperty(notes = "상점명", name = "storeName", dataType="String", example="\"형준상회\"")
        private String storeName;
        @ApiModelProperty(notes = "상점 기본 주소", name = "basicAddress", dataType="String", example="\"서울 구로구 디지털로 31길 90\"")
        private String basicAddress;
        @ApiModelProperty(notes = "상점 상세 주소", name = "detailAddress", dataType="String", example="\"형준 아파트 108동 1004호\"")
        private String detailAddress;
        @ApiModelProperty(notes = "상점 지역코드", name = "regionCode", dataType="Long", example="08371")
        private Long regionCode;
        @ApiModelProperty(notes = "상점 위도", name = "latitude", dataType="Float", example="18.31")
        private Float latitude;
        @ApiModelProperty(notes = "상점 경도", name = "longitude", dataType="Float", example="23.12")
        private Float longitude;
        @ApiModelProperty(notes = "상점 전화번호", name = "callNumber", dataType="String", example="\"010-2548-6707\"")
        private String callNumber;
        @ApiModelProperty(notes = "지역이름(Ex. 서울 또는 서울 구로구 ~~~)", name = "regionName", dataType="String", example="\"서울\"")
        private String regionName;

}

