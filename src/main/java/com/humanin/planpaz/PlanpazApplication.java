package com.humanin.planpaz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlanpazApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanpazApplication.class, args);
	}

}
