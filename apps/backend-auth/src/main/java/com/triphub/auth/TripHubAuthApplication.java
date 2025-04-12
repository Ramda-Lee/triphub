package com.triphub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class TripHubAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripHubAuthApplication.class, args);
	}

}
