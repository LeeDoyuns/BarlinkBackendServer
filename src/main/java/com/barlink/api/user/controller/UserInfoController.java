package com.barlink.api.user.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.barlink.api.user.service.UserBuyListDrinkDetailService;
import com.barlink.dto.board.UserBuyListDto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.ResetException;
import com.barlink.api.drink.service.DrinkDetailService;
import com.barlink.api.exception.RestException;
import com.barlink.api.user.service.UserFavoriteDrinkDetailService;
import com.barlink.api.user.service.UserInfoService;
import com.barlink.api.user.service.UserLoginService;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.common.CommonTokenCheck;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.drink.DrinkDetail;
import com.barlink.domain.user.User;
import com.barlink.domain.user.UserFavoriteDrinkDetail;
import com.barlink.dto.user.UserFavoriteDTO;
import com.barlink.dto.user.UserInfoDTO;
import com.barlink.dto.user.UserPage;
import com.barlink.dto.user.inpt.FavoriteList;
import com.barlink.dto.user.inpt.UserEmail;
import com.barlink.dto.user.inpt.UserNickName;
import com.barlink.dto.user.inpt.UserUpdatePasswordDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiKeyAuthDefinition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 유저 정보(정보수정,조회,찜 관련) 컨트롤러
 * @author LeeDoYun
 *
 */

@RestController
@RequestMapping("/api/user")
@Api(tags = "User : 유저 정보 관련 기능")
public class UserInfoController {

	private Logger logger = LoggerFactory.getLogger(UserLoginController.class);
	
	@Autowired 
	private UserInfoService service;
	
	@Autowired
	private UserLoginService userService;
	
	@Autowired
	private CommonRequestBodyWrapper commonWrapper;
	
	@Autowired
	private DrinkDetailService drinkDetailService;
	
	@Autowired
	private UserFavoriteDrinkDetailService favService;

	@Autowired
	private UserBuyListDrinkDetailService userBuyListDrinkDetailService;
	
	/*
	 * 에러 처리 메세지 기본값
	 */
	String resMsg = "처리 중 에러 발생";
	
	@PostMapping("/userInfo")
	@ApiOperation(value="유저 기본정보 조회",notes = "user의 이메일,닉네임 리턴")
	@ApiResponses({
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=200,message="조회 성공")
	})
	public ResponseEntity<Map<String,Object>> getUserInfo(HttpServletRequest request,HttpServletResponse response) throws RestException{
		Map resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		int resCode = response.getStatus();
		
		try {
			//세션 확인 성공시 
			if(resCode==200) {
				//accessToken정보로 user정보 parsing 하여 확인
				String token = request.getHeader("Authorization").replace("Bearer ", "");
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				
				if(userInfoMap.get("email") != null && !"".equals(userInfoMap.get("email"))) {
					User user = service.selectUserInfo(userInfoMap.get("email").toString());
					Map userMap = new HashMap();

					userMap.put("email", user.getEmail());
					userMap.put("nickName", user.getNickName());
					
					resultMap.put("user", userMap);
					response.setStatus(200);
				}else if(response.getStatus()==401){
					throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
				}else {
					//403반환
					response.setStatus(response.SC_FORBIDDEN);
				}
			}
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, "확인되지 않는 유저",500);
		}
		
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@PutMapping("/updateNickName")
	@ApiOperation(value="닉네임 변경")
	@ApiResponses({
		@ApiResponse(code=200,message = "닉네임 변경 성공",response = HashMap.class),
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=500,message = "서버 에러",response = HashMap.class)
	})
	public ResponseEntity<Map<String,Object>> updateNickName(HttpServletRequest request, HttpServletResponse response,@RequestBody UserNickName nickName) throws RestException{	
		Map<String,Object> resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		if(response.getStatus()==200) {
			try {
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				
				logger.info("==updateNickName=="+userInfoMap);
				
				User user = service.selectUserInfo(userInfoMap.get("email").toString());
				
				
				user.setNickName(nickName.getNickName());
				userService.updateNickName(user);
				
				UserInfoDTO userInfoDTO = userService.parsingUserInfo(user);
				
				//AcessToken은 닉네임,권한,이메일 정보가 담겨있으므로 재발급 한다.
				String accessToken = JwtUtil.createToken(String.valueOf(user.getUserId()), userInfoDTO);
				String refreshToken = JwtUtil.createRefreshToken(String.valueOf(user.getUserId()), userInfoDTO);
				
				resultMap.put("accessToken", accessToken);
				resultMap.put("refreshToken", refreshToken);
				
				//update후 변경된 닉네임을 response에 담는다
				resultMap.put("nickName", userInfoDTO.getNickName());
				resultMap.put("result", true);
				
				response.setStatus(response.SC_OK);
				
			} catch (Exception e) {
				throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
			}
		}else if(response.getStatus()==401){
			throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
		}else {
			resultMap.put("result", false);
			response.setStatus(response.SC_OK);
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	
	
	
	@PutMapping("/updatePassword")
	@ApiOperation(value="비밀번호 변경")
	@ApiResponses({
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=200,message = "비밀번호 변경 성공",response = HashMap.class)
	})
	public ResponseEntity<Map<String,Object>> updatePassword(HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "password", required = true,value = "변경 전 비밀번호 확인 후 비밀번호 업데이트") @RequestBody UserUpdatePasswordDTO pwd) throws RestException{	
		Map resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		int resCode = response.getStatus();
		
		
		try {
			//세션 확인 성공시 
			if(resCode==200) {
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				
				logger.info("==updatePassword=="+userInfoMap);
				
				String encodeBeforePwd = pwd.getBeforePassword();
				
				User user = service.selectUserInfo(userInfoMap.get("email").toString(),encodeBeforePwd);
				if(user == null) {
					resMsg =  "이전 비밀번호 정보가 맞지 않습니다.";
					 throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
				}
				
				user = service.updatePassword(user,pwd.getUpdatePassword() );
				
				//정보가 바뀐다면, refreshToken과 accessToken을 재발급 하고 클라이언트에 전송해준다.
				userInfoMap.clear();
				
				UserInfoDTO userInfoDTO = new UserInfoDTO();
				userInfoDTO.setEmail(user.getEmail());
				userInfoDTO.setNickName(user.getNickName());
				userInfoDTO.setUserRole(user.getUserRole());
				
				resMsg = "비밀번호 변경 완료";
				resultMap.put("message",resMsg );
				
			}else if(response.getStatus()==401){
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				resultMap.put("result",false);
				resMsg = "비밀번호 변경 실패";
				resultMap.put("message",resMsg );
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
			
		}
			
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	@PutMapping("/leaveUser")
	@ApiOperation(value="탈퇴 처리 ( 사용여부 N으로 변경 ) ")
	@ApiResponses({
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=200,message = "탈퇴 처리 성공",response = HashMap.class),
		@ApiResponse(code=500,message="서버 에러")
	})
	public ResponseEntity<Map<String,Object>> leaveUser(HttpServletRequest request, HttpServletResponse response) throws RestException{
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		try {
			if(response.getStatus()==200) {
				
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				
				logger.info("==leaveUser=="+userInfoMap);
			
				User user = service.selectUserInfo(userInfoMap.get("email").toString());
				user = service.leaveUser(user);
				
				if(user!=null && user.getUseStatus().equals("N")) {
					response.setStatus(response.SC_OK);
					resultMap.put("result", true);
				}else {
					response.setStatus(response.SC_OK);
					resultMap.put("result", false);
				}
				
			}else if(response.getStatus()==401){
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				response.setStatus(response.SC_OK);
				resultMap.put("result", false);
			}
		}catch(Exception e) {
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	@PostMapping("/registerFavoriteDrinkDetail/{drinkDetailId}")
	@ApiOperation(value="찜하기")
	@ApiResponses({
		@ApiResponse(code=200,message = "찜목록 저장 성공",response = HashMap.class),
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=403,message="찜목록 저장 실패")
	})
	public ResponseEntity<Map<String,Object>> registerFavorite(HttpServletRequest request,HttpServletResponse response, @PathVariable long drinkDetailId) throws RestException{
		
		Map resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		try {
			
			if(response.getStatus()==200) {
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				User user = service.selectUserInfo(userInfoMap.get("email").toString());
				
					
					DrinkDetail board = drinkDetailService.findById((int) drinkDetailId);
					
					UserFavoriteDrinkDetail fav = new UserFavoriteDrinkDetail();
					fav.setUser(user);
					fav.setDetail(board);
					
					//저장
					fav = favService.save(fav);
					
					if(fav!=null ) {
						response.setStatus(response.SC_OK);
						resultMap.put("result", true);
					}else {
						response.setStatus(response.SC_OK);
						resultMap.put("result", false);
					}
			}else if(response.getStatus()==401){
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				response.setStatus(200);
				resultMap.put("result", false);
				resultMap.put("message", resMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
		}
	
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@DeleteMapping("/deleteFavoriteDrinkDetail")
	@ApiOperation(value="찜한 구매정보 삭제 - 다중삭제")
	@ApiResponses({
		@ApiResponse(code=200,message = "찜목록 삭제 성공/실패",response = HashMap.class),
		@ApiResponse(code=401,message = "토큰 재발급 필요"),
		@ApiResponse(code=403,message="파라미터 누락")
	})
	public ResponseEntity<Map<String,Object>> deleteFavorite(HttpServletRequest request,HttpServletResponse response
			,@ApiParam(value="상세정보 번호 [1,2,3] 형태로 다중 입력. ") @RequestBody long[] favoriteList ) throws RestException{
		
		Map resultMap = new HashMap();
		JSONObject json;
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		List favoriteIdList = Arrays.asList(favoriteList);
		
		
	
		if(response.getStatus()==200) {
			try {
				
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				logger.info("==registerFavorite=="+userInfoMap);
				User user = service.selectUserInfo(userInfoMap.get("email").toString());
				
				if(favoriteIdList.isEmpty()) {
					response.setStatus(response.SC_OK);
					resMsg ="입력값이 없습니다.";
					resultMap.put("message", resMsg);
					resultMap.put("result", false);
				}else {
					Map param = new HashMap();
					param.put("user", user);
					param.put("favoriteIdList", favoriteIdList);
					
					boolean result = favService.deleteFavorite(param);
					
					if(result) {
						resultMap.put("result", true);
					}else {
						resultMap.put("result", false);
					}
				}
				
				
			} catch (Exception e) {
				throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
			}
		}else if(response.getStatus()==401){
			throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
		}else {
			response.setStatus(response.SC_OK);
			resultMap.put("result", false);
		}
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	@PostMapping("/getFavoriteList")
	@ApiOperation(value="찜목록 조회")
	@ApiResponses({
		@ApiResponse(code=200,message = "찜목록 조회",response = HashMap.class),
		@ApiResponse(code=401,message = "토큰 재발급 필요")
	})
	public ResponseEntity<Map<String,Object>> getFavoriteList(HttpServletRequest request,HttpServletResponse response) throws RestException{
		Map resultMap = new HashMap();
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		Map userInfoMap = (Map) request.getAttribute("userInfoMap");
		try {
			if(response.getStatus()==200) {
				User user = service.selectUserInfo(userInfoMap.get("email").toString());
				
				List<UserFavoriteDTO> favList = favService.selectList(user);
				resultMap.put("list", favList);
				resultMap.put("result", true);
				response.setStatus(response.SC_OK);
			}else {
				response.setStatus(200);
				resultMap.put("result", false);
				resultMap.put("message", "토큰정보가 유효하지 않습니다.");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
		}
		
	
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}

	@GetMapping("/buyList")
	@ApiOperation(value="내가 작성한 구매정보 List", notes="내가 구매한 주류 정보들 List")
	@ApiResponses({
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code=401,message = "토큰 재발급 필요"),
			@ApiResponse(code = 500, message = "서버 에러 -> 로그 확인")
	})
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getBuyList(HttpServletRequest request, HttpServletResponse response,UserPage page) throws RestException{
		Map result = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		try {
			if(response.getStatus()==200){
				result.put("result", true);
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				User user = service.selectUserInfo(userInfoMap.get("email").toString());

				PageRequest pages = PageRequest.of(page.getPage()-1, page.getPageSize());
				
				Page<UserBuyListDto> userBuyListDto = userBuyListDrinkDetailService.selectUserBuyList(user,pages);
				result.put("buy_list", userBuyListDto);
				result.put("total_count", userBuyListDto.getPageable());
				
			}else if(response.getStatus()==401){
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				response.setStatus(200);
				result.put("result", false);
			}
				
		}catch(Exception e) {
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
		}


		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.valueOf(response.getStatus()));
	}
	
	/*
	@PostMapping("/sendUpdatePasswordAuthMail")
	@ApiOperation(value="sendUpdatePasswordEmail", tags="비밀번호 찾기 시 이메일 인증.")
	@ApiResponses({
						@ApiResponse(code=200, message = "이메일 발송 성공,혹은 실패 여부.  true/false"),
						@ApiResponse(code=500, message = "해당 이메일 주소로 등록 된 유저 정보 없음")
				   })
	public ResponseEntity<Map<String,Object>> sendMail(HttpServletRequest req, HttpServletResponse res,@ApiParam(required = true, value="찾을 이메일 주소") @RequestBody UserEmail email) throws RestException{
		
		Map resultMap = new HashMap();
		if(email.getEmail()==null) 
			throw new RestException(HttpStatus.BAD_REQUEST, "이메일 입력 필수",res.SC_FORBIDDEN);
		
		User user = service.selectUserInfo(email.getEmail());
		if(user == null) {
			throw new RestException(HttpStatus.BAD_REQUEST, "확인되지 않는 유저",500);
		}else {
			try {
				boolean result = service.sendMail(email.getEmail());
				if(result==true) {
					resultMap.put("result", true);
				}else {
					resultMap.put("result", false);
				}
			}catch(Exception e) {
				throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "server Error - 이메일 발송 실패.", 500);
			}
			//존재하는 유저라면 인증테이블에 insert후 메일 보낸다.
			
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(res.getStatus()));
	}*/
	
}
