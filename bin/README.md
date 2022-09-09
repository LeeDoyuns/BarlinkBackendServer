### Barlink - backend API Server

### 프로젝트 셋

1. 개발 언어 : openjdk 11
   
2. 개발 API 서버 : spring boot 2.4.5

3. DB : mysql 8.0

4. DB Connection : JdbcTemplate

5. 테스트 서버 : AWS EC2

### 개발 순서

1. 테이블 모델링 - 논리 & 물리

2. API 문서화 - swagger 

3. 기능 분담 이후 각 기능들 상세 정리

4. 기능 구현

5. 테스트 & 배포

### 기능 정리

✅ 로그인 & 회원가입 & 마이페이지

✅ 메인화면(메인에 한번에 API ? 소 카테고리를 누를때마다 호출 ?) & 소 카테고리에서 술 선택 시 → 상세화면에 필요한 데이터 기능 구현

✅ 상세화면 → 용량 변경 버튼 & 지역 선택 기능 버튼 구현

✅ 상세화면 → 더보기 버튼 클릭 시 해당 술 정보 기능 구현

✅ Header → 구매정보등록 url 이동 → 술 구매정보 등록 기능 구현

✅ Header → 찜한 술 url 이동시 → 찜한 정보 기능 구현

...또 뭘 정리할까