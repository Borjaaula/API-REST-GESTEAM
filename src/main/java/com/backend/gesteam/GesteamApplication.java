package com.backend.gesteam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GesteamApplication {

	public static void main(String[] args) {
		SpringApplication.run(GesteamApplication.class, args);
	}

}
