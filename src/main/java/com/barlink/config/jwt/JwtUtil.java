package com.barlink.config.jwt;

import java.security.Key;
import java.util.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.barlink.domain.user.User;
import com.barlink.dto.user.UserInfoDTO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


/**
 * Jwt 토큰생성 유틸. 토큰 생성과 파싱 가능.
 * @author LeeDoyun
 *
 */

@Component
@ConfigurationProperties("jwt")
public class JwtUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);
	
	private static String secret ;
	
	private static String secretref;
	
	//apiKey
	private static String secretApiKey = "kw3xWc8lvpDx^IkOw4SxoK45*e2#1mHsc6Jwok4%ckmNups54CI8";
	
	
	public  String getSecret() {
		return secret;
	}

	public  void setSecret(String secret) {
		JwtUtil.secret = secret;
	}
	
	public String getSecretref() {
		return secretref;
	}
	
	public void setSecretref(String secretref) {
		JwtUtil.secretref = secretref;
	}


	public static  String createToken(String subject,UserInfoDTO user) {
		SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
		
		//secretKey에 refreshToken이라는 문자열 들어감.
		
		byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary("accessToken"+secretApiKey+secret);
		Key signKey = new SecretKeySpec(secretKeyBytes, algorithm.getJcaName());
		JwtBuilder builder = Jwts.builder()
						.setSubject(subject)
						.setIssuer("access_barlink")
						.setHeaderParam("type", "JWT")
						.claim("permission", true)
						.signWith(algorithm, signKey);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(user!=null) {
			builder.claim("access", user);
		}
		
		//토큰 만료시간 설정 : 30분 후
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(now);
		cal.add(Calendar.HOUR,1 );
		Date times = cal.getTime();
		
		builder.setExpiration(times);
		return builder.compact();
		
	}
	
	public static  String createRefreshToken(String subject,UserInfoDTO user) {
		SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;
		
		byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretref+"_refreshToken"+secretApiKey);
		Key signKey = new SecretKeySpec(secretKeyBytes, algorithm.getJcaName());
		JwtBuilder builder = Jwts.builder()
						.setSubject(subject)
						//도메인명
						.setIssuer("refresh_barlink")
						.setHeaderParam("type", "JWT")
						.claim("permission", true)
						.signWith(algorithm, signKey);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(user!=null) {
			builder.claim("access", user);
		}
		
		//토큰 만료시간 설정 : 14일
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_WEEK, 14);
		Date times = cal.getTime();
		
		builder.setExpiration(times);
		return builder.compact();
		
	}
	
	
	/*
	 * accessToken parsing
	 */
	
	public static  String getSubject (String token) {
		Claims claim = Jwts.parser()
						.setSigningKey(DatatypeConverter.parseBase64Binary("accessToken"+secretApiKey+secret))
						.parseClaimsJws(token).getBody();
		return claim.getSubject();
	}
	
	public static  Claims getTokenBody (String token) {
		Claims claim = Jwts.parser()
						.setSigningKey(DatatypeConverter.parseBase64Binary("accessToken"+secretApiKey+secret))
						.parseClaimsJws(token).getBody();
		return claim;
	}
	
	
	public static  Map<String,Object> getSubjectMap(String token){
		String subject = getSubject(token);
		Map<String,Object> map = new LinkedHashMap<>();
		map.put("result", subject);
		return map;
	}
	
	
	
	/*
	 * refreshToken parsing
	 */
	
	public static  String getRefreshSubject (String token) {
		Claims claim = Jwts.parser()
						.setSigningKey(DatatypeConverter.parseBase64Binary(secretref+"_refreshToken"+secretApiKey))
						.parseClaimsJws(token).getBody();
		return claim.getSubject();
	}
	
	public static  Claims getRefreshTokenBody (String token) {
		Claims claim = Jwts.parser()
						.setSigningKey(DatatypeConverter.parseBase64Binary(secretref+"_refreshToken"+secretApiKey))
						.parseClaimsJws(token).getBody();
		logger.info("parsing RefreshToken = "+claim.toString());
		return claim;
	}
	
	public static  Map<String,Object> getRefreshSubjectMap(String token){
		String subject = getRefreshSubject(token);
		Map<String,Object> map = new LinkedHashMap<>();
		map.put("result", subject);
		return map;
	}
	
	
}
