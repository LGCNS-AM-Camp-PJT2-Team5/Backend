package com.team9.jobbotdari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@RefreshScope
@EnableFeignClients
@SpringBootApplication
public class JobbotdariUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobbotdariUserApplication.class, args);
	}

}
