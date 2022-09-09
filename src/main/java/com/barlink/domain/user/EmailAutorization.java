package com.barlink.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="email_authorization")
@Data
@NoArgsConstructor
public class EmailAutorization {
	
	@EmbeddedId
	private EmailPK emailType;
	
	@Column(name="AUTHORIZATION_CODE")
	private String code;
	
	@Column(name="create_date")
	private LocalDateTime createDate;
	
	@Column(name="update_date")
	private LocalDateTime updateDate;
	
	
	
	public void setDateNow() {
		this.createDate = LocalDateTime.now();
		this.updateDate = LocalDateTime.now();
	}
	
}
