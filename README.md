### Barlink - backend API Server

### 프로젝트 셋

1. 개발 언어 : openjdk 11
   
2. 개발 API 서버 : spring boot 2.4.5

3. DB : MariaDB

4. DB Connection : JdbcTemplate

5. 테스트 서버 : AWS EC2

6. 배포 : Docker

7. 소스 관리 : Github

8. Skill : JPA,JWT,Spring-Security



### 개발 순서

1. 테이블 모델링 - 논리 & 물리

2. API 문서화 - swagger 

3. 기능 분담 이후 각 기능들 상세 정리

4. 기능 구현

5. 테스트 & 배포




### 기능 정리

https://docs.google.com/spreadsheets/d/1KrMdU2zxbMkW-nqszPGJDkr-UVjPRMPSG2m28HUfOaM/edit#gid=0


### AWS 

ip -> 3.36.66.69
domain -> barlink.co.kr

### 차후 고려

docker deploy



###역할 분담

✅ 유저 관련 기능 : 이도윤

✅ 주류 관련 기능 : 김성진

✅ 관리자 기능 : 이도윤(주류,게시글),김성진(유저)

✅ 서버구축 : 김성진

✅ DB서버 세팅 : 이도윤


---

### 향후 일정 정리

- [ ] 유저 전체 기능 마무리 (관리자 기능 포함) & 테스트 

- [ ] 주류 관련 기능 마무리 (관리자 기능 포함) & 테스트

- [ ] 오류 수정 & 리팩토링 작업

- [ ] 추가 기능 작업 진행
