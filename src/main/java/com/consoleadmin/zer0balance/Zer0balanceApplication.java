package com.consoleadmin.zer0balance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Zer0balanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(Zer0balanceApplication.class, args);
	}

}
