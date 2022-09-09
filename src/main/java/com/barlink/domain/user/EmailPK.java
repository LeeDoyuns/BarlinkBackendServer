package com.barlink.domain.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class EmailPK implements Serializable{
	@Column(name = "email")
	private String email;
	
	@Column(name="type")
	private String type;
 
}
