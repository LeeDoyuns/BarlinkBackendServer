package com.barlink.api.user.service.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.barlink.domain.user.EmailAutorization;
import com.barlink.domain.user.EmailPK;
import com.barlink.domain.user.User;
import com.barlink.dto.user.EmailCheckDTO;

/**
 * JoinRepository
 * @author LeeDoyun
 *
 */
@Repository
public interface EmailRepository  extends JpaRepository<EmailAutorization,Object>{

	EmailAutorization findByEmailTypeAndCode(EmailPK pk, String code);

	Optional<EmailAutorization> findByEmailType(EmailPK pk);


}
 