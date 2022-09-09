package com.barlink.api.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.barlink.api.user.service.UserJoinService;
import com.barlink.config.common.CommonEncoder;
import com.barlink.domain.user.EmailAutorization;
import com.barlink.domain.user.EmailPK;
import com.barlink.domain.user.User;
import com.barlink.dto.user.EmailCheckDTO;
import com.barlink.dto.user.EmailDTO;
import com.google.common.base.Charsets;

import lombok.extern.slf4j.Slf4j;

/**
 * 유저 가입 관련 서비스
 * @author LeeDoyun
 *
 */
@Service
@Slf4j
public class UserJoinServiceImpl implements UserJoinService{

	
	@Autowired
	private UserJoinRepository joinRepo;
	
	/*
	 * password encoder
	 */
	private CommonEncoder encoder = new CommonEncoder();
	
	
	
	@Autowired
	private EmailRepository emailRepo;
	
	
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
	
	
	/**
	 * 회원가입
	 */
	@Override
	@Transactional
	public User insertUser(User vo) {
		
		//Default로 권한은 user로 설정
		vo.setUserRole("USER");
		
		//password암호화
		vo.setPassword(encoder.encode(vo.getPassword()));
		
		vo.setCreateDate(LocalDateTime.now());
		vo.setUpdateDate(LocalDateTime.now());
		
		/*
		 * saveAndFlush메소드를 사용하면 key값을 vo객체에 반환받아올 수 있다.
		 * save()는 반환받지 못한다.
		 */
		try {
			vo = joinRepo.saveAndFlush(vo);
		}catch(UnexpectedRollbackException e) {
			vo = null;
			return vo;
		}
		return vo;
	}



	@Override
	public User findByEmail(String email) {
		return joinRepo.findByEmailAndUseStatus(email,"Y");
	}



	/**
	 * @param to : 이메일주소
	 */
	@Override
	@Transactional
	public boolean sendEmail(String to) {
		//checkNum = 6자리 인증번호
		Random random = new Random();
		int checkNum = random.nextInt(888888) + 111111;
		
		LocalDateTime date = LocalDateTime.now();
		
		

		EmailAutorization email = new EmailAutorization();
		EmailPK emails = new EmailPK();
		emails.setEmail(to);
		emails.setType("join");
		
		email.setEmailType(emails);
		email.setCode(String.valueOf(checkNum));
		email.setUpdateDate(date);
		
		//기존에 인증된 이력이 있는지 확인한 후, 존재한다면 updateDate만 업데이트 한다.
		Optional<EmailAutorization> before = emailRepo.findByEmailType(emails);
		
		
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
		
		
		
		/*
		EmailDTO dto = EmailDTO.builder()
				.to(to)
				.subject(subject)
				.content(content)
				.build();
		SendEmailResult result = null;
		try {
			result = amazonSimpleEmailService.sendEmail(dto.toSendRequestDTO());
			
		}catch(AccountSendingPausedException e) {
			log.error("AWS 인증 오류 - Paused");
			e.printStackTrace();
			return false;
		}

		return sendingResultMustSuccess(result,to);
		*/
		return result;
		
		
		
		
	}
	


	@Override
	public boolean checkAuthCode(EmailCheckDTO check) {
		EmailPK pk = new EmailPK();
		pk.setEmail(check.getEmail());
		pk.setType("join");
		
		EmailAutorization result = emailRepo.findByEmailTypeAndCode(pk,check.getCode());
		if(result==null) {
			return false;
		}else {
			return true;
		}
	}



	@Override
	public List<User> findByNickName(String nickName) {
		return joinRepo.findByNickName(nickName);
	}

}
