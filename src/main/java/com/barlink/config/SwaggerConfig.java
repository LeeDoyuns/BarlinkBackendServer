package com.barlink.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.google.common.collect.Lists;

import springfox.documentation.builders.*;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.*;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Parameter;

/**
 * ?��?�� : Swagger 기본 ?��?��
 * @author LeeDoYun
 *
 */


@Configuration
@EnableSwagger2
public class SwaggerConfig {
	

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet(); 
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Barlink API Documentation")
                .description("[Barlink] API. 인증이 필요한 기능 사용 시 Authorize 버튼 클릭해서 로그인 처리 후 api 이용")
                .version("1.0")
                .termsOfServiceUrl("https:barlink.co.kr/")
                .build();
    }
    
    /*토큰 관련 설정*/
    private ApiKey apiKey() {
        return new ApiKey("Bearer +accessToken", "Authorization", "header");
//        return new ApiKey("JWT","Authorization", "header");

    }
    
    @Bean
    public Docket api() throws UnknownHostException {
        
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.barlink.api"))
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .securityContexts(Arrays.asList(securityContext()))
        		.securitySchemes(Arrays.asList(apiKey()));
    }
    
    private SecurityContext securityContext() {
    	return SecurityContext.builder() 
    			.securityReferences(defaultAuth()) 
    			.forPaths(PathSelectors.any()) .build(); 
    	} 
    
    List<SecurityReference> defaultAuth() {
    	AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
    	AuthorizationScope[] authorizationScopes = new AuthorizationScope[1]; 
    	authorizationScopes[0] = authorizationScope; 
    	return Lists.newArrayList(new SecurityReference("Bearer +accessToken", authorizationScopes));
    	
    }
    
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }




}
