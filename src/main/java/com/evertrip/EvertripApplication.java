package com.evertrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EvertripApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvertripApplication.class, args);
	}

}
