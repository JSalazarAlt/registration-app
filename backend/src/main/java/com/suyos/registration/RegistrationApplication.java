package com.suyos.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the user registration and authentication system.
 * 
 * This class serves as the entry point for the Spring Boot application,
 * enabling auto-configuration and component scanning for the entire application.
 * 
 * @author Joel Salazar
 */
@SpringBootApplication
public class RegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationApplication.class, args);
	}

}
