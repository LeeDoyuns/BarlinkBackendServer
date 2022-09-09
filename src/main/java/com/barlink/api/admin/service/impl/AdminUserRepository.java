package com.barlink.api.admin.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.barlink.domain.user.User;

public interface AdminUserRepository extends JpaRepository<User,Object> {

	@Query(value="select count(us.userId) from users us ")
	int getTotalUserCount();

	@Query(value="select count(us.userId) from users us where us.createDate between :start and :end")
	int getTodayUserCount(LocalDateTime start, LocalDateTime end);

}
