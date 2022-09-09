package com.barlink.api.user.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barlink.api.exception.RestException;
import com.barlink.api.user.service.UserJoinService;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.domain.user.User;
import com.barlink.dto.user.EmailCheckDTO;
import com.barlink.dto.user.inpt.UserEmail;
import com.barlink.dto.user.inpt.UserJoinDTO;
import com.barlink.dto.user.inpt.UserNickName;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;



/**
 * 회원가입 컨트롤러
 * @author LeeDoYun
 *
 */
@RestController
@RequestMapping("/api/user")
@Api(tags = "User : 회원가입 관련 기능")
@Slf4j
public class UserJoinController {
	
	private Logger logger = LoggerFactory.getLogger(UserLoginController.class);
	
	@Autowired 
	private UserJoinService joinService;
	
	@Autowired
	private CommonRequestBodyWrapper commonWrapper;
	
	/*
	 * 기본값 Message
	 */
	String resMsg = "서버에서 에러가 발생하였습니다. ";
	
	
	/**
	 * 회원가입 
	 * RequestBodyWrapper클래스를 이용해 requestBody를 읽어온 후 json parsing하여 사용한다.
	 * @param request
	 * @param response
	 * @return Response : Map(message)
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@PostMapping(value="/join")
	@ApiOperation(value="회원가입", notes="createPage 는 추적용으로 사용된다.")
	@ApiResponses({
		@ApiResponse(code=200,message="회원가입 성공/실패",response = ResponseEntity.class),
	})
	public ResponseEntity<Map<String,Object>> joinUser(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(name="회원가입",value="회원정보")@Valid @RequestBody UserJoinDTO userJoin){
		
		
		//Response객체
		ResponseEntity<Map<String,Object>> result = null;
		//응답코드set
		Map resultMap = new HashMap();
		
		try {
				User vo = userJoin.toEntity();
				logger.debug("params {}",userJoin.toString());
				
				vo = joinService.insertUser(vo);
				
				response.setStatus(response.SC_OK);

				if(vo !=null && vo.getUserId()>0) {
					resMsg = "회원가입 성공";
					resultMap.put("message",resMsg);
					resultMap.put("result",true);
				}else {
					resMsg = "회원가입 실패";
					resultMap.put("result",false);
				}
			
		}catch(Exception e) {
			response.setStatus(200);
			resultMap.put("result", false);
			resultMap.put("message", "회원가입 중 에러 발생");
			return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
		}
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	
	
	@PostMapping(value="/checkEmailAddress")
	@ApiOperation(value="이메일 사용 가능 여부")
	@ApiResponses({
		@ApiResponse(code=200,message="true / false",response = Map.class),
	})
	@Transient
	public ResponseEntity<Map<String,Object>> checkEmail(HttpServletRequest request,HttpServletResponse response
			, @ApiParam(name="email",required = true)@RequestBody UserEmail email) throws RestException {
		Map resultMap = new HashMap();

//			유저가 있는지 조회
		try {
			
			User user = joinService.findByEmail(email.getEmail());
			response.setStatus(response.SC_OK);
			if(user==null) {
				resultMap.put("result",true );
			}else {
				resultMap.put("result",false );
			}
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, resMsg,500);
		}
			
		
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	@PostMapping(value="/emailAuthCode")
	@ApiOperation(value="인증번호 발송", notes = "response : result key값에 true,false반환")
	@ApiResponses({
		@ApiResponse(code=200,message="인증코드 발송 성공",response = Map.class),
		@ApiResponse(code=500,message="인증코드 발송 실패",response =  Map.class)
	})
	public ResponseEntity<Map<String,Object>> sendAuthCode(HttpServletRequest request,HttpServletResponse response,
			@ApiParam(name="email", value="이메일주소") @RequestBody UserEmail email ) throws RestException {
		
		Map map  = new HashMap();
		int statusCode=500;
		
		try {
			
			boolean result = joinService.sendEmail(email.getEmail());
			map.put("result", result);
			
			
			if(result) {
				statusCode=200;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			throw new RestException(HttpStatus.BAD_REQUEST, "이메일 발송 에러",500);
		}
		
		
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.valueOf(statusCode));
	}
	
	@PostMapping(value="/checkEmailAuthCode")
	@ApiOperation(value="인증코드 일치 여부")
	@ApiResponses({
		@ApiResponse(code=200,message="인증코드 일치 여부",response = Map.class),
	})
	public ResponseEntity<Map<String,Object>> checkEmailAuthCode(HttpServletRequest request,HttpServletResponse response
			,@RequestBody EmailCheckDTO check) throws RestException{
		Map map = new HashMap();
		int resultCode=500; 
		boolean result = false;
		
		try {
			result = joinService.checkAuthCode(check);
			resultCode=200;
			
			if(result) {
				map.put("result", true);
			}else {
				map.put("result", false);
			}
			
		}catch(Exception e) {
			throw new RestException(HttpStatus.BAD_REQUEST, resMsg,500);
		}
		
		map.put("result", result);
		
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.valueOf(resultCode));
	}
	
	@PostMapping(value="/checkDuplicateNickName")
	@ApiOperation(value="닉네임 사용 가능 여부(중복되면 false)")
	@ApiResponses({
		@ApiResponse(code=200,message="닉네임 사용 가능 여부",response = Map.class),
	})
	public ResponseEntity<Map<String,Object>> checkDuplicateNickName(HttpServletRequest request,HttpServletResponse response
			,@RequestBody UserNickName nickName) throws RestException{
		Map map = new HashMap();
		int resultCode=500; 
		if(nickName.getNickName() == null || "".equals(nickName.getNickName())) {
			throw new RestException(HttpStatus.BAD_REQUEST, "파라미터 (nickName) 누락. ",response.SC_FORBIDDEN);
		}else {
			try {
				resultCode=200;
				List<User> user = joinService.findByNickName(nickName.getNickName());
				if(user == null || user.size()==0 ) {
					map.put("result", true);
				}else {
					map.put("result", false);
				}
			}catch(Exception e) {
				e.printStackTrace();
				throw new RestException(HttpStatus.BAD_REQUEST, resMsg,500);
			}
			
		}
		
		return new ResponseEntity<Map<String,Object>>(map,HttpStatus.valueOf(resultCode));
	}

}
