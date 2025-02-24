package com.team9.jobbotdari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@SpringBootApplication
public class JobbotdariApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobbotdariApplication.class, args);
	}

}
