db세팅 스크립트

db생성 전 캐릭터셋 utf8로 통일 필수

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `create_page` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `nick_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `use_status` varchar(255) NOT NULL,
  `user_role` varchar(255) NOT NULL,
  `refresh_token` text DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8
;


시퀀스(jpa연동을 위한)생성 필수

create table hibernate_sequence(
	sequence_name CHAR(1) ,
	next_val BIGINT not null
);


insert into test.hibernate_sequence(next_val) values(1);





;



create table drink_category(
	category_seq int primary key ,
	category_name varchar(20) not null
);





users테이블의 seq는 auto_increase 사용중.
테이블 데이터를 날린 후 seq값을 초기화 하고 싶다면
ALTER TABLE users AUTO_INCREMENT =1;



주류
CREATE TABLE `drink` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `drink_name` varchar(255) DEFAULT NULL,
  `drink_id` bigint(20) DEFAULT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6t8wblip6n7cueihkvmnglob9` (`drink_id`),
  KEY `FK164hjp0c726aa5pf9y17e7tq6` (`file_id`),
  CONSTRAINT `FK164hjp0c726aa5pf9y17e7tq6` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`),
  CONSTRAINT `FK6t8wblip6n7cueihkvmnglob9` FOREIGN KEY (`drink_id`) REFERENCES `drink_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

;

주류 카테고리
CREATE TABLE `drink_category` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `category_name` varchar(255) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jcokvvbjjv4fc3fwehcg73fln` (`category_name`),
  KEY `FKmseoslux5inmojgadklmaywr1` (`parent_id`),
  CONSTRAINT `FKmseoslux5inmojgadklmaywr1` FOREIGN KEY (`parent_id`) REFERENCES `drink_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

;

CREATE TABLE `drink_info` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `buy_date` datetime(6) DEFAULT NULL,
  `cost` int(11) DEFAULT NULL,
  `create_program_id` varchar(255) DEFAULT NULL,
  `is_access` char(1) DEFAULT NULL,
  `is_deleted` char(1) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `volume` int(11) DEFAULT NULL,
  `drink_info_id` bigint(20) DEFAULT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  `store_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8u1od6ti0ih33kvp3i2irkfyu` (`drink_info_id`),
  KEY `FKf3v01xenr6c4mstr47x6t5e8v` (`file_id`),
  KEY `FK90po5trhktyvv6nwfcxdk235u` (`store_id`),
  CONSTRAINT `FK8u1od6ti0ih33kvp3i2irkfyu` FOREIGN KEY (`drink_info_id`) REFERENCES `drink` (`id`),
  CONSTRAINT `FK90po5trhktyvv6nwfcxdk235u` FOREIGN KEY (`store_id`) REFERENCES `store` (`id`),
  CONSTRAINT `FKf3v01xenr6c4mstr47x6t5e8v` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

주류 숙성 연도
CREATE TABLE `drink_year` (
  `id` bigint(20) NOT NULL,
  `age` varchar(255) DEFAULT NULL,
  `drink_year_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa2ki5fe4bmkd8gntjfx7bct83` (`drink_year_id`),
  CONSTRAINT `FKa2ki5fe4bmkd8gntjfx7bct83` FOREIGN KEY (`drink_year_id`) REFERENCES `drink` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

;
파일
CREATE TABLE `file` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `create_program_id` varchar(255) DEFAULT NULL,
  `file_type` char(1) DEFAULT NULL,
  `origin_file_name` varchar(255) DEFAULT NULL,
  `save_file_name` varchar(255) DEFAULT NULL,
  `save_file_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

상점 정보
CREATE TABLE `store` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `basic_address` varchar(255) DEFAULT NULL,
  `call_number` varchar(255) DEFAULT NULL,
  `call_number2` varchar(255) DEFAULT NULL,
  `detail_address` varchar(255) DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `region_code` int(11) DEFAULT NULL,
  `store_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

CREATE TABLE `volume` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `drink_volume_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5dk4w6kq4qpudfwsuq1s78ht6` (`drink_volume_id`),
  CONSTRAINT `FK5dk4w6kq4qpudfwsuq1s78ht6` FOREIGN KEY (`drink_volume_id`) REFERENCES `drink` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;


CREATE TABLE `board` (
  `BOARD_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '게시글 ID(SEQ)값',
  `DRINK_ID` bigint(20) NOT NULL COMMENT '주류ID값',
  `CATEGORY_ID` bigint(20) NOT NULL COMMENT '주류 카테고리ID',
  `FILD_ID` bigint(20) DEFAULT NULL COMMENT '파일 ID값(FK)',
  `STORE_ID` bigint(20) NOT NULL COMMENT '상점 ID값(FK)',
  `USER_ID` bigint(20) NOT NULL COMMENT '유저 ID값(FK)',
  `REGION_CD` bigint(20) NOT NULL COMMENT '지역ID값(FK)',
  `NOTE` text DEFAULT NULL COMMENT '게시글 내용',
  `BUY_DATE` char(8) DEFAULT NULL COMMENT '구입일.(EX:20210601)',
  `DELETE_YN` char(1) NOT NULL DEFAULT 'N' COMMENT '숨김/삭제 여부 - 숨김 : H, 삭제 : D, 둘다 아님 : N',
  `ACCESS_YN` char(1) NOT NULL DEFAULT 'N' COMMENT '승인여부 (EX: 승인 : ''Y'', 거절:''N'')',
  `CREATE_DATE` date NOT NULL DEFAULT current_timestamp() COMMENT '등록일',
  `UPDATE_DATE` datetime NOT NULL DEFAULT current_timestamp() COMMENT '글 수정일',
  `CREATE_PAGE` varchar(100) NOT NULL COMMENT '등록 페이지 명',
  `COST` bigint(20) NOT NULL COMMENT '구매 가격',
  `VOLUMN` int(11) DEFAULT NULL COMMENT '주류 용량',
  PRIMARY KEY (`BOARD_ID`),
  KEY `BOARD_FK_DRINK_ID` (`DRINK_ID`),
  KEY `BOARD_FK_FILE_ID` (`FILD_ID`),
  KEY `BOARD_FK_2` (`CATEGORY_ID`),
  KEY `BOARD_FK_3` (`STORE_ID`),
  CONSTRAINT `BOARD_FK_2` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `drink_category` (`id`),
  CONSTRAINT `BOARD_FK_3` FOREIGN KEY (`STORE_ID`) REFERENCES `store` (`id`),
  CONSTRAINT `BOARD_FK_DRINK_ID` FOREIGN KEY (`DRINK_ID`) REFERENCES `drink` (`id`),
  CONSTRAINT `BOARD_FK_FILE_ID` FOREIGN KEY (`FILD_ID`) REFERENCES `file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='게시글(주류 구매 정보 게시글) 테이블'
;







=========================================================
세션관리
SessionCheckFilter.java
LoginFilter.java

filter 자체에서 return하는 response Code,Message 등이 있으므로 UserController_회원가입 메소드에서 참고.


response status ==> 만료 토큰 : 401
그 이외 다른 것 (잘못된 결과값이라던가? 그런것들은 403 반환)