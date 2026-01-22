package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EntityScan("ru.otus.hw.models")
@EnableJpaRepositories("ru.otus.hw.repositories")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
