package com.devee.devhive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DevHiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevHiveApplication.class, args);
	}

}
