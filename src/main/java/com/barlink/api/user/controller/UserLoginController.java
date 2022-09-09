package com.barlink.api.user.controller;
import java.io.IOException;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.barlink.api.user.service.UserLoginService;
import com.barlink.config.common.CommonRequestBodyWrapper;
import com.barlink.config.common.CommonTokenCheck;
import com.barlink.config.common.RequestBodyWrapper;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.user.User;
import com.barlink.dto.user.UserInfoDTO;
import com.barlink.dto.user.inpt.RefreshTokenDTO;
import com.barlink.dto.user.inpt.UserLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;


/**
 * 20210513 이도윤 작성
 * 유저 회원가입, 로그인 등 세션과 회원가입에 대한 부분 처리를 담당하는 Controller
 * @author LeeDoyun
 *
 */


@RestController
@RequestMapping("/api/user")
@Api(value="swaggerTest")
public class UserLoginController {
	
	private Logger logger = LoggerFactory.getLogger(UserLoginController.class);
	
	@Autowired 
	private UserLoginService service;
	
//	@Autowired
	private CommonRequestBodyWrapper commonWrapper = new CommonRequestBodyWrapper();
	
	
	
	@PostMapping(value="/login" )
	@ApiOperation(value="Login Function", tags="로그인")	//api문서에 표기될 정보
	@ApiResponses({	//응답 형태 
						@ApiResponse(code = 200, message = "암호화된 유저 번호, Message"),
				   })
	public ResponseEntity<Map<String,Object>> getUserInfo(HttpServletRequest request,HttpServletResponse response,
				 @RequestBody UserLoginDTO users
			) throws ServletException, IOException, ParseException {
		Map result = new HashMap<String, Object>();
		
		
		try {
			
			String email = users.getEmail();
			String password = users.getPassword();
			//id로 유저 정보 조회
			User user = service.findByEmail(email);
			
			//loginFilter에 객체 전달
			request.setAttribute("user",user);	//seq값
			request.setAttribute("inputPassword",password);
			
			//LoginFilter를 태우기 위해 forward처리
			RequestDispatcher rq = request.getRequestDispatcher("/api/user/loginResult");
			rq.forward(request, response);
		}catch(Exception e) {
			result.put("message", "Not found this user");
			
		}
		
		return new ResponseEntity<Map<String,Object>>(result,HttpStatus.FORBIDDEN);
	}
	
	/**
	 * login filter에서 dbConnection부분이 안되서 forward하게 함.
	 */
	@PostMapping(value="/loginResult")
	@ApiOperation(value="Login_2", notes = "토큰 발급 관련 필터 처리를 위한 url.")
	public ResponseEntity<Map<String,Object>> loginResult(HttpServletRequest request,HttpServletResponse response) {
		ResponseEntity<Map<String,Object>> res=null;
		Map<String,Object> result = new HashMap<>();
		
		//LoginFilter에서 return한 refreshToken db에 update
		User user = (User) request.getAttribute("user");
		service.updateRefreshToken(user);
		
		//20210602 - responsebody에 실어보냄. 추후 header로 바뀔 수도 있음!
		if(response.getStatus()==200) {
			result.put("accessToken", request.getAttribute("accessToken"));
			result.put("refreshToken", user.getUserToken());
			result.put("user", request.getAttribute("userInfo"));
			result.put("result", true);
		}else {
			response.setStatus(200);
			result.put("result", false);
		}
		result.put("message", response.getHeader("message"));
		
		res=new ResponseEntity<Map<String,Object>>(result,HttpStatus.valueOf(response.getStatus()));
		
		return res;
	}
	
	
	
	/**
	 * access토큰 재발급
	 * @param request
	 * @param response
	 * @return 토큰 정보
	 * @throws IOException
	 * @throws ParseException
	 */
	@PostMapping(value="/issueAccessToken")
	@ApiOperation(value="AccessToken 재발급", tags="토큰 재발급",notes="401에러 확인 시 refreshToken 전송")
	public ResponseEntity<Map<String,Object>> issueAccessToken(HttpServletRequest request,HttpServletResponse response,@RequestBody RefreshTokenDTO refreshToken) throws IOException, ParseException{
		Map<String, Object> resultMap = new HashMap();
		Map<String, String> param = new HashMap();
		
		
		if(response.getStatus()==200) {
			
			param.put("refreshToken",refreshToken.getRefreshToken());
			//refresh토큰 정보가 올바른 정보인지 확인.
			boolean bool = service.checkAccessToken(param);
			
			
			String userId = JwtUtil.getRefreshSubject(refreshToken.getRefreshToken());	
			
			
			Optional<User> users = service.findByIdAndRefreshToken(Long.parseLong(userId),refreshToken.getRefreshToken());
			User user = users.get();
			
			UserInfoDTO userInfoDTO = new UserInfoDTO();

			userInfoDTO.setEmail(user.getEmail());
			userInfoDTO.setUserRole(user.getUserRole());
			userInfoDTO.setNickName(user.getNickName());
			
			if(bool) {
				//토큰 정보가 일치한다면 재발급
				response.setStatus(response.SC_OK);
				String token = JwtUtil.createToken(userId, userInfoDTO);
				resultMap.put("accessToken", token);
				resultMap.put("result", true);
			}else {
				//토큰정보가 일치하지 않는다면 에러 반환
				response.setStatus(response.SC_OK);
				resultMap.put("result", false);
				resultMap.put("message", "invalid refreshToken");
			}
			
		}
		
			
		return new ResponseEntity<Map<String,Object>>(resultMap,HttpStatus.valueOf(response.getStatus()));
	}
	
	
	
	/**
	 * 컨트롤러 내에서 DTO에 parameter validation이 충족되지 않을경우 해당 에러메세지 리턴
	 * @param parameter
	 * @param bindingResult
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object CustomMethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
		
		String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
		Map map = new HashMap<>();
		map.put("message", message);
		
		return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
	}
	
	
}
