package com.barlink.dto.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import lombok.Builder;
import lombok.Data;

/**
 * 유저가 쓴 글 목록. user와 board 테이블을 join해서 가져온 결과를 저장한다.
 * @author Doyun
 *
 */
@Builder
@Data
public class UserBoardDTO {
	
	private int boardId;
	
	private String drinkName;
	
	private String categoryName;
	
	private String filePath;
	
	private String storeName;
	
	private String accessYn;
	
	private int volumn;
	
	private int cost;
	
	private String deleteYn;
	
	private LocalDateTime createDate;
	
	private LocalDateTime updateDate;
	
	
	
	
	
	
	
	
}
