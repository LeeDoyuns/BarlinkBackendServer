package com.barlink.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.barlink.config.SwaggerConfig;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import(SwaggerConfig.class)
@EntityScan(basePackages = {"com.barlink.params", "com.barlink.domain"})
@ComponentScan(basePackages = "com.barlink")
@EnableJpaAuditing

public class ApiServerApplication extends SpringBootServletInitializer{
	
	private static Logger logger = LoggerFactory.getLogger(ApiServerApplication.class);
	
	public static void main(String[] args) {
		
		SpringApplication.run(ApiServerApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return super.configure(builder);

	}
	
	
	

}
