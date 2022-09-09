package com.barlink.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
public class QueryDslConfiguration {

	@PersistenceContext
	private EntityManager manager;
	
	@Bean
	public JPAQueryFactory factory() {
		return new JPAQueryFactory(manager);
	}
}
