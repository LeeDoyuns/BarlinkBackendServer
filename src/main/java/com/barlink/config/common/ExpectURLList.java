package com.barlink.config.common;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 세션검증(암호화된 user번호) 제외 url 목록
 * 최초작성 : 21.05.28
 * @author Doyun
 *
 */
@Component
public class ExpectURLList {
	
	/**
	 *일반 유저들이 이용하는 모든 url주소에 admin 이라는 텍스트는 절대 들어가면 안됨. 
	 *모든 관리자 기능은 url에 admin 이 들어가도록 함.
	 */
	
	private static final String loginUrl = "/api/user/login";	//login url
	
	private static final String testViewUrl = "/apiTest";	//api 테스트페이지 이동 url
	
	private static final String signUpUrl = "/api/user/join";	//회원가입url

	private static final String aithorizationEmail = "/api/user/emailAuthCode";	//이메일 인증 관련.

	private static final String testSession = "/api/user/session";	//sessionTest
	
	private static final String loginRedirectUrl = "/api/user/loginResult";	//token 발급 로직

	private static final String drinkCategoryUrl = "/api/drink/categories";

	private static final String drinkInfoUrl = "/api/drink/info";

	private static final String drinkNewUrl = "/api/drink/new";
	
	private static final String checkEmail = "/api/user/checkEmailAddress";	//이메일 중복 체크
	
	private static final String issueToken = "/api/user/issueAccessToken";	//토큰 재발급
	
	private static final String registDetail = "/registerFavoriteDrinkDetail/";
	
	
	
	/*
	 * 세션검증에서 제외할 url목록 반환
	 */
	public ArrayList<String> getUrlList(){ 
		ArrayList<String> urlList = new ArrayList();
		urlList.add(loginUrl);
		urlList.add(testViewUrl);
		urlList.add(signUpUrl);
		urlList.add(testSession);
		urlList.add(loginRedirectUrl);
		urlList.add(drinkCategoryUrl);
		urlList.add(drinkNewUrl);
		urlList.add(drinkInfoUrl);
		urlList.add(checkEmail);
		urlList.add(issueToken);
		urlList.add(aithorizationEmail);
		urlList.add(registDetail);
		
		return urlList;
	}
	
	
}
