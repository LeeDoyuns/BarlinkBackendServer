package com.barlink.api.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.barlink.api.user.service.UserInfoService;
import com.barlink.config.common.CommonEncoder;
import com.barlink.config.jwt.JwtUtil;
import com.barlink.domain.user.EmailAutorization;
import com.barlink.domain.user.EmailPK;
import com.barlink.domain.user.User;
import com.google.common.base.Charsets;


/**
 * 유저 정보 관련 서비스
 * @author LeeDoyun
 *
 */
@Service
public class UserInfoServiceImpl implements UserInfoService{
	
	private Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
	
	@Autowired
	private CommonEncoder encoder = new CommonEncoder();

	@Autowired
	private UserLoginRepository userLoginRepo;
	
	@Autowired
	private EmailRepository emailRepo;
	
	
	@Override
	public User selectUserInfo(String email) {
		return userLoginRepo.findByEmailAndUseStatus(email,"Y");
	}
	
	
	@Value("${sendmail.id}")
	private String accessId;
	
	@Value("${sendmail.passwd}")
	private String accessKey;
	
	@Value("${sendmail.port}")
	private int port;
	
	@Value("${sendmail.host}")
	private String host;
	
	@Value("${sendmail.domain}")
	private String domain;

	@Override
	public User updatePassword(User user, String password) {
		String encodedPwd = encoder.encode(password);
		
		user.setPassword(encodedPwd);
		return userLoginRepo.saveAndFlush(user);
	}

	@Override
	public Map parseingUserInfo(String string) {
		//accessToken정보로 user정보 parsing 
		String token = string.replace("Bearer ", "");
		
		Map tokenMap = JwtUtil.getTokenBody(token);
		
		Map userInfoMap = (Map) tokenMap.get("access");
		
		return userInfoMap;
	}

	@Override
	public User leaveUser(User user) {
		user.setUseStatus("N");
		
		return userLoginRepo.saveAndFlush(user);
	}

	@Override
	public boolean sendMail(String to) {
		Random random = new Random();
		int checkNum = random.nextInt(888888) + 111111;
		
		LocalDateTime date = LocalDateTime.now();
		
		

		EmailAutorization email = new EmailAutorization();
		
		EmailPK pk = new EmailPK();
		pk.setEmail(to);
		pk.setType("pswd");
		
		email.setEmailType(pk);
		email.setCode(String.valueOf(checkNum));
		email.setUpdateDate(date);
		
		//기존에 인증된 이력이 있는지 확인한 후, 존재한다면 updateDate만 업데이트 한다.
		Optional<EmailAutorization> before = emailRepo.findByEmailType(pk);
		
		
		if(before.isPresent()) {
			email.setCreateDate(before.get().getCreateDate());
		}else {
			email.setCreateDate(date);
		}
		
									
		
		emailRepo.save(email);
		
		
		String message = "</br> <h3>barlink</h3> </br> "
				+ "인증번호 : <h4 style=\"display:inline-block\">"+checkNum+"</h4>";
	
		String subject = "[barlink.co.kr] 인증번호";
		
		return sendMail(to,subject,message);
	}
	
	/**
	 * 메일 발송관련 메소드
	 * 
	 */
	private boolean sendMail(String to, String subject, String content) {
String from = "noreply@barlink.co.kr";
		
		
		boolean result = false;
		
		try {
			Properties prop = new Properties();
			prop.put("mail.smtp.host", host);
			prop.put("mail.smtp.port",  port);
			prop.put("mail.smtp.auth", "true");
			prop.put("mail.smtp.starttls.enable", "true");
			prop.put("mail.smtp.ssl.trust", domain);
			
			Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(accessId,accessKey);
					}
				}
			);
			
			
			JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
			javaMailSender.setJavaMailProperties(prop);
			javaMailSender.setSession(session);
			final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			
			boolean html = true;
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, Charsets.UTF_8.displayName());
			helper.setFrom(new InternetAddress(from, domain));
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, html);	
			javaMailSender.send(mimeMessage);
			result=true;
			
		}catch(MessagingException e) {
			result=false;
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result=false;
		}
		
		return result;
	}
	

	@Override
	public User selectUserInfo(String userEmail, String encodeBeforePwd) {
		
		User user = userLoginRepo.findByEmail(userEmail);
		
		if(encoder.matches(encodeBeforePwd, user.getPassword())) {
			return  user;
		}else {
			return null;
		}
		
	}
	
}
