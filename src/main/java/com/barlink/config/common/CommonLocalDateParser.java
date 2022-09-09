package com.barlink.config.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


/**
 * 문자형식의 Data( "yyyy-mm-dd") 를 LocalDateTime 클래스로 변경하는 메소드
 * @author Doyun
 *
 */
public class CommonLocalDateParser {
	
	/**
	 * @Description 파라미터를 LocalDateTime으로 변환. 0시 00분 으로 set해서 리턴해주는 method
	 * @param date("yyyy-MM-dd")
	 * @return
	 */
	public  static LocalDateTime startDateTime (String date) {
		LocalDate ld = LocalDate.parse(date);
		return ld.atStartOfDay();
	}
	
	/**
	 * @Description 파라미터를 LocalDateTime으로 변환. 23시 59분 59초 으로 set해서 리턴해주는 method
	 * @param date("yyyy-MM-dd")
	 * @return
	 */
	public  static LocalDateTime endDateTime (String date) {
		LocalDate ld = LocalDate.parse(date);
		return LocalDateTime.of(ld, LocalTime.of(23,59,59));
	}
	
	/**
	 * @Description
	 * @return
	 */
	public static LocalDateTime todayStartDateTime() {
		LocalDate ld = LocalDate.now();
		return ld.atStartOfDay();
	}
	
	public static LocalDateTime todayEndDateTime() {
		LocalDate ld = LocalDate.now();
		return LocalDateTime.of(ld, LocalTime.of(23, 59, 59));
	}
	
	
	
	
}
