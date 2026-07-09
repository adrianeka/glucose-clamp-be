package com.tujuhsembilan.glucoseclamp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = { "com.tujuhsembilan.glucoseclamp", "lib.i18n", "lib.minio"})
@EnableJpaAuditing
public class GlucoseclampApplication {

	@PostConstruct
	public void init() {
		// Set Spring Boot SetTimeZone
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Jakarta"));
	}
	public static void main(String[] args) {
		SpringApplication.run(GlucoseclampApplication.class, args);
	}

}
