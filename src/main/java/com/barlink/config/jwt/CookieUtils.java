
package com.barlink.config.jwt;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

/**
 * 최초 로그인 시 쿠키를 생성하여 프론트에 전달하고 서버에서는 refresh토큰값을 db에 저장한다.
 * 만약, access토큰이 만료되었다면 refresh토큰값을 가지고있는 쿠키를 조회해 db의 값과 비교한다.
 * 존재한다면, access토큰을 재발급 한 후 cookie를 다시 내려준다. 
 * 
 * 프론트에선 매 요청시 쿠키에서 accessToken값을 header에 포함해서 보낸다.
 * 
 * @author Doyun
 *
 */
@Component
public class CookieUtils {
	
	public static Cookie createAccessTokenCookie(String cookieName,String token) {
		Cookie cookie= new Cookie(cookieName, token);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60*30); //쿠키 유효시간 30분
		cookie.setPath("/api/"); 	//모든 api에 쿠키 전송
		return cookie;
	}
	
	//고민을 해보자.
	public static Cookie createRefreshTokenCookie(String cookieName,String token) {
		
		Cookie cookie = new Cookie(cookieName,token);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60*60*24*3);	// (3일간 유지)
		cookie.setPath("/api/user/login");		//로그인시에만 refreshToken을 전송한다.
		return cookie;
		
	}
	
	

}
