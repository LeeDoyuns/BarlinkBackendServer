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
 * ?????? ??????(????????????,??????,??? ??????) ????????????
 * @author LeeDoYun
 *
 */

@RestController
@RequestMapping("/api/user")
@Api(tags = "User : ?????? ?????? ?????? ??????")
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
	 * ?????? ?????? ????????? ?????????
	 */
	String resMsg = "?????? ??? ?????? ??????";
	
	@PostMapping("/userInfo")
	@ApiOperation(value="?????? ???????????? ??????",notes = "user??? ?????????,????????? ??????")
	@ApiResponses({
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=200,message="?????? ??????")
	})
	public ResponseEntity<Map<String,Object>> getUserInfo(HttpServletRequest request,HttpServletResponse response) throws RestException{
		Map resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		int resCode = response.getStatus();
		
		try {
			//?????? ?????? ????????? 
			if(resCode==200) {
				//accessToken????????? user?????? parsing ?????? ??????
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
					//403??????
					response.setStatus(response.SC_FORBIDDEN);
				}
			}
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, "???????????? ?????? ??????",500);
		}
		
		
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	@PutMapping("/updateNickName")
	@ApiOperation(value="????????? ??????")
	@ApiResponses({
		@ApiResponse(code=200,message = "????????? ?????? ??????",response = HashMap.class),
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=500,message = "?????? ??????",response = HashMap.class)
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
				
				//AcessToken??? ?????????,??????,????????? ????????? ?????????????????? ????????? ??????.
				String accessToken = JwtUtil.createToken(String.valueOf(user.getUserId()), userInfoDTO);
				String refreshToken = JwtUtil.createRefreshToken(String.valueOf(user.getUserId()), userInfoDTO);
				
				resultMap.put("accessToken", accessToken);
				resultMap.put("refreshToken", refreshToken);
				
				//update??? ????????? ???????????? response??? ?????????
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
	@ApiOperation(value="???????????? ??????")
	@ApiResponses({
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=200,message = "???????????? ?????? ??????",response = HashMap.class)
	})
	public ResponseEntity<Map<String,Object>> updatePassword(HttpServletRequest request, HttpServletResponse response,
			@ApiParam(name = "password", required = true,value = "?????? ??? ???????????? ?????? ??? ???????????? ????????????") @RequestBody UserUpdatePasswordDTO pwd) throws RestException{	
		Map resultMap = new HashMap();
		
		response = CommonTokenCheck.checkAuthToken(request, response);
		
		int resCode = response.getStatus();
		
		
		try {
			//?????? ?????? ????????? 
			if(resCode==200) {
				Map userInfoMap = (Map) request.getAttribute("userInfoMap");
				
				logger.info("==updatePassword=="+userInfoMap);
				
				String encodeBeforePwd = pwd.getBeforePassword();
				
				User user = service.selectUserInfo(userInfoMap.get("email").toString(),encodeBeforePwd);
				if(user == null) {
					resMsg =  "?????? ???????????? ????????? ?????? ????????????.";
					 throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
				}
				
				user = service.updatePassword(user,pwd.getUpdatePassword() );
				
				//????????? ????????????, refreshToken??? accessToken??? ????????? ?????? ?????????????????? ???????????????.
				userInfoMap.clear();
				
				UserInfoDTO userInfoDTO = new UserInfoDTO();
				userInfoDTO.setEmail(user.getEmail());
				userInfoDTO.setNickName(user.getNickName());
				userInfoDTO.setUserRole(user.getUserRole());
				
				resMsg = "???????????? ?????? ??????";
				resultMap.put("message",resMsg );
				
			}else if(response.getStatus()==401){
				throw new RestException(HttpStatus.UNAUTHORIZED, resMsg, 401);
			}else {
				resultMap.put("result",false);
				resMsg = "???????????? ?????? ??????";
				resultMap.put("message",resMsg );
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, resMsg, 500);
			
		}
			
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	@PutMapping("/leaveUser")
	@ApiOperation(value="?????? ?????? ( ???????????? N?????? ?????? ) ")
	@ApiResponses({
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=200,message = "?????? ?????? ??????",response = HashMap.class),
		@ApiResponse(code=500,message="?????? ??????")
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
	@ApiOperation(value="?????????")
	@ApiResponses({
		@ApiResponse(code=200,message = "????????? ?????? ??????",response = HashMap.class),
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=403,message="????????? ?????? ??????")
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
					
					//??????
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
	@ApiOperation(value="?????? ???????????? ?????? - ????????????")
	@ApiResponses({
		@ApiResponse(code=200,message = "????????? ?????? ??????/??????",response = HashMap.class),
		@ApiResponse(code=401,message = "?????? ????????? ??????"),
		@ApiResponse(code=403,message="???????????? ??????")
	})
	public ResponseEntity<Map<String,Object>> deleteFavorite(HttpServletRequest request,HttpServletResponse response
			,@ApiParam(value="???????????? ?????? [1,2,3] ????????? ?????? ??????. ") @RequestBody long[] favoriteList ) throws RestException{
		
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
					resMsg ="???????????? ????????????.";
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
	@ApiOperation(value="????????? ??????")
	@ApiResponses({
		@ApiResponse(code=200,message = "????????? ??????",response = HashMap.class),
		@ApiResponse(code=401,message = "?????? ????????? ??????")
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
				resultMap.put("message", "??????????????? ???????????? ????????????.");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR,resMsg, 500);
		}
		
	
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}

	@GetMapping("/buyList")
	@ApiOperation(value="?????? ????????? ???????????? List", notes="?????? ????????? ?????? ????????? List")
	@ApiResponses({
			@ApiResponse(code = 200, message = "success"),
			@ApiResponse(code=401,message = "?????? ????????? ??????"),
			@ApiResponse(code = 500, message = "?????? ?????? -> ?????? ??????")
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
	@ApiOperation(value="sendUpdatePasswordEmail", tags="???????????? ?????? ??? ????????? ??????.")
	@ApiResponses({
						@ApiResponse(code=200, message = "????????? ?????? ??????,?????? ?????? ??????.  true/false"),
						@ApiResponse(code=500, message = "?????? ????????? ????????? ?????? ??? ?????? ?????? ??????")
				   })
	public ResponseEntity<Map<String,Object>> sendMail(HttpServletRequest req, HttpServletResponse res,@ApiParam(required = true, value="?????? ????????? ??????") @RequestBody UserEmail email) throws RestException{
		
		Map resultMap = new HashMap();
		if(email.getEmail()==null) 
			throw new RestException(HttpStatus.BAD_REQUEST, "????????? ?????? ??????",res.SC_FORBIDDEN);
		
		User user = service.selectUserInfo(email.getEmail());
		if(user == null) {
			throw new RestException(HttpStatus.BAD_REQUEST, "???????????? ?????? ??????",500);
		}else {
			try {
				boolean result = service.sendMail(email.getEmail());
				if(result==true) {
					resultMap.put("result", true);
				}else {
					resultMap.put("result", false);
				}
			}catch(Exception e) {
				throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, "server Error - ????????? ?????? ??????.", 500);
			}
			//???????????? ???????????? ?????????????????? insert??? ?????? ?????????.
			
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(res.getStatus()));
	}*/
	
}
