package com.barlink.config.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.barlink.api.exception.RestException;
import com.barlink.config.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * @desc 공통으로 쓰이는 토큰 유효성 검사 클래스
 * @ 2021.08.19 leedy 생성		
 * @author Doyun
 *
 */
public class CommonTokenCheck {

	
	
	/**
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	public static HttpServletResponse checkAuthToken(HttpServletRequest req,HttpServletResponse res) throws RestException {
		
		if(req.getHeader("Authorization") == null) {
			res.setStatus(res.SC_NOT_ACCEPTABLE);
			res.addHeader("message", "need Login or userToken");
			throw new RestException(HttpStatus.BAD_REQUEST, "access token이 존재하지 않습니다.",res.SC_NON_AUTHORITATIVE_INFORMATION);
		}
		
		
		String userToken = req.getHeader("Authorization").replace("Bearer ", "");
		
		String path = req.getServletPath();
		
		try {
			res.setStatus(res.SC_OK);
			
			//token이 존재하지 않는다면
			if( userToken==null || "".equals(userToken) ) {
				res.setStatus(res.SC_NOT_ACCEPTABLE);
				res.addHeader("message", "need Login or userToken");
				
			}else {
				//token이 존재한다면 parsing 해서 userId로 유저 정보 찾음
				Map<String, Object> token = JwtUtil.getTokenBody(userToken);
				
				String domain = (String) token.get("iss");
				
				if(!domain.equals("access_barlink")) {
					throw new RestException(HttpStatus.NOT_ACCEPTABLE, "accessToken이 아닙니다.",res.SC_NOT_ACCEPTABLE);
				}
				
				Map userInfoMap = (Map) token.get("access");
				String userRole = (String) userInfoMap.get("userRole");
				
				req.setAttribute("userInfoMap", userInfoMap);
				
				//권한 체크
				if(path.contains("admin") && !userRole.equals("ADMIN")) {
					throw new RestException(HttpStatus.BAD_REQUEST, "관리자만 사용 가능한 기능힙니다.",res.SC_NOT_ACCEPTABLE);
				}
				
			}
		}catch(ExpiredJwtException e) {
			//토큰이 만료된 경우 처리
			throw new RestException(HttpStatus.BAD_REQUEST, "accessToken이 만료되었습니다.",res.SC_UNAUTHORIZED);
		}catch(NullPointerException e) {
			throw new RestException(HttpStatus.BAD_REQUEST, "서버에서 처리 중 에러가 발생했습니다.",res.SC_INTERNAL_SERVER_ERROR);
		}catch(Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, "토큰 정보가 유효하지 않습니다.",res.SC_INTERNAL_SERVER_ERROR);
		}
		
		return res;
	}
}
