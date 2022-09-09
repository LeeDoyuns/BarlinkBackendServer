package com.barlink.dto.user;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class EmailDTO {
	
	/*
	 * 보내는 사람은 admin@barlink.co.kr 로 고정.
	 */
	private static final String from = "admin@barlink.co.kr";
	
	
	//받는 사람
	private String to;
	
	//제목
	private String subject;
	
	//본문
	private String content;
	
	@Builder
	public EmailDTO(String to, String subject, String content) {
		this.to=to;
		this.subject=subject;
		this.content=content;
	}
	
	public SendEmailRequest toSendRequestDTO() {
		Destination destination = new Destination().withToAddresses(this.to);
		Message message = new Message().withSubject(createContents(this.subject))
					.withBody(new Body().withHtml(createContents(content)));

		SendEmailRequest req = new SendEmailRequest()
				.withSource(from)
				.withDestination(destination)
				.withMessage(message);
		
		return req;
	}
	
	public Content createContents(String content) {
		return new Content().withCharset("UTF-8")
					.withData(content);
	}
	

}
