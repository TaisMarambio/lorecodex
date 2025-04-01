package com.lorecodex.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.lorecodex.backend.repository")
public class LoreCodexApplication {
	public static void main(String[] args) {
		SpringApplication.run(LoreCodexApplication.class, args);
	}
}
