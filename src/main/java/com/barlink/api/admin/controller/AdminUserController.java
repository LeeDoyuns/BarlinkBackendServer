package com.barlink.api.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barlink.api.admin.service.AdminUserService;
import com.barlink.api.exception.RestException;
import com.barlink.api.user.service.UserLoginService;
import com.barlink.config.common.CommonTokenCheck;
import com.barlink.domain.user.User;
import com.barlink.dto.admin.user.AdminUpdateUserPassword;
import com.barlink.dto.admin.user.AdminUserInfo;
import com.barlink.dto.admin.user.AdminUserList;
import com.barlink.dto.admin.user.AdminUserPage;
import com.barlink.dto.board.UserBuyListDto;
import com.barlink.dto.user.AdminUserInfoDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 관리자 유저 관리 컨트롤러
 * @author LeeDoYun
 *
 */
@RestController
@RequestMapping("/api/admin")
@Api(tags="Admin : 유저 관련 기능")
public class AdminUserController {
	
	@Autowired
	private UserLoginService userService;
	
	@Autowired
	private AdminUserService adminUserService;
	
	private String resMsg = "서버 에러가 발생하였습니다.";	//기본메세지 (500error)
	
	
	@PostMapping("/changeUserPassword/{userId}")
	@ApiOperation(value="어드민  유저 비밀번호 강제 변경", notes = "userId 필수 입력. 이메일주소 아님.")
	public ResponseEntity<Map<String,Object>> getUserInfo(HttpServletRequest request,HttpServletResponse response,
			@PathVariable long userId
			, @ApiParam(name="changePassword",value="변경할 비밀번호", required = true) @RequestBody AdminUpdateUserPassword changePassword) throws RestException{
		Map resultMap = new HashMap();
		
		//토근 확인
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
			int resCode = response.getStatus();
			//세션 확인 성공시 
			if(resCode==200) {
				
				//변경할 비밀번호와 유저 ID
				Optional<User> opt = userService.findById(userId);
				User user = opt.get();
				
				user = adminUserService.chenageBycryptPassword(user,changePassword.getPassword());
				
				response.setStatus(response.SC_OK);
				if(user != null) {
					resultMap.put("message", "password change Success!");
					resultMap.put("result", true);
					
				} else {
					resultMap.put("message", "password change Fail");
					resultMap.put("result", false);
				}
				
			}else if(resCode == 401) {	//accessToken 만료
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 500);
			}
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 500);
		}
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	/*
	@GetMapping("/makeTestUser")
	@ApiOperation(tags="admin - 테스트 유저 생성", value="테스트 유저 생성")
	public  ResponseEntity<Map<String,Object>> makeTestUser(HttpServletRequest request,HttpServletResponse response){
		
		Map<String,Object> resultMap = new HashMap();
		
		adminUserService.makeTestUser();
		
		resultMap.put("result", true);
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}*/
	
	@PostMapping("/getUserList")
	@ApiOperation(notes = "어드민 - 유저목록 조회. start/endDate 미입력시 날짜 데이터는 탐색하지 않고 전체 유저정보 가져옴.", value = "유저목록 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공/실패"),
		@ApiResponse(code = 401, message="유효하지 않은 토큰 - 토큰 만료"),
		@ApiResponse(code = 500, message="기타 서버 에러")
	})
	public ResponseEntity<Map<String,Object>> getUserList(HttpServletRequest request,HttpServletResponse response, @RequestBody AdminUserList param
				) throws RestException{
		
		Map resultMap = new HashMap<String,Object>();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
			response.setStatus(response.SC_OK);
			/*
			 * 2021.10.07 페이징 주석처리. 추후 유저 늘어나면 사용할 수 있음.
			 * */
//				PageRequest page = PageRequest.of(param.getPage()-1, param.getPageSize(), Sort.by(Sort.DEFAULT_DIRECTION,"userId"));
//				Page<AdminUserInfoDTO> userList = adminUserService.selectAllUser(param,page);
			Map<String,Object> result =  adminUserService.selectAllUser(param);
			
			resultMap.put("result", true);
			resultMap.put("object", result);
				
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
			
		}
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@PostMapping("/userBuyList/{userId}")
	@ApiOperation(value="유저가 등록한 구매정보 조회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공/실패"),
		@ApiResponse(code = 401, message="유효하지 않은 토큰 - 토큰 만료"),
		@ApiResponse(code = 500, message="기타 서버 에러")
	})
	public ResponseEntity<Map<String,Object>> getUserList(HttpServletRequest request,HttpServletResponse response,@PathVariable long userId,@RequestBody AdminUserPage commonPage) throws RestException{
		
		Map resultMap = new HashMap<String,Object>();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
//			PageRequest pages = PageRequest.of(commonPage.getPage()-1, commonPage.getPageSize());
//			Page<UserBuyListDto> buyList = adminUserService.selectUserBuyList(userId,pages);
			List<UserBuyListDto> buyList = adminUserService.selectUserBuyList(userId);
			resultMap.put("result", true);
			resultMap.put("list", buyList);
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@PostMapping("/updateUserInfoByAdmin")
	@ApiOperation(value="유저 정보 강제 변경", notes="userId만 필수 입력. useStatus를 N으로 변경하면 탈퇴처리됨.")
	public ResponseEntity<Map<String,Object>> updateUserInfo(HttpServletRequest request,HttpServletResponse response, @RequestBody AdminUserInfo info) throws RestException{
		
		Map<String,Object> resultMap = new HashMap();
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
			if(info.getUserId() == 0) {
				throw new RestException(HttpStatus.UNAUTHORIZED, "변경할 유저 정보가 존재하지 않습니다.", 500);
			}else {
				
				Optional<User> opt = userService.findById(info.getUserId());
				User user = opt.get();
				
				user = adminUserService.updateUserInfo(user, info);
				
				response.setStatus(200);
				
				if(user==null) {
					resultMap.put("message", "변경 실패");
					resultMap.put("result", false);
				}else {
					resultMap.put("message", "변경 성공");
					resultMap.put("result", true);
				}
			}
			
		}catch(Exception e) {
			resultMap.put("message", "변경 실패");
			resultMap.put("result", false);
		}
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@PostMapping("/userCnt")
	@ApiOperation(notes = "총 유저수, 당일 가입 유저 수 조회",value = "유저수 조회")
	public ResponseEntity<Map<String,Object>> getUserCount(HttpServletRequest request,HttpServletResponse response) throws RestException{
		Map<String,Object> resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
			resultMap = adminUserService.selectUserCount();
			response.setStatus(200);
			resultMap.put("result",true);
			
		}catch(Exception e) {
			response.setStatus(200);
			resultMap.put("result", false);
		}
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}

}
