package com.example.smartwaste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartwasteApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartwasteApplication.class, args);
	}

}
