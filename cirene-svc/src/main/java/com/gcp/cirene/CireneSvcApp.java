package com.gcp.cirene;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class CireneSvcApp {

	public static void main(String[] args) {
		SpringApplication.run(CireneSvcApp.class, args);
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
			CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
			loggingFilter.setIncludeClientInfo(true);
			loggingFilter.setIncludeQueryString(true);
			loggingFilter.setIncludePayload(false);
			loggingFilter.setIncludeHeaders(false);
			return loggingFilter;
	}

}
