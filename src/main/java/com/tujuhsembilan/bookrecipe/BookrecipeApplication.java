package com.tujuhsembilan.bookrecipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = { "com.tujuhsembilan.bookrecipe", "lib.i18n", "lib.minio"})
@EnableJpaAuditing
public class BookrecipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookrecipeApplication.class, args);
	}

}
