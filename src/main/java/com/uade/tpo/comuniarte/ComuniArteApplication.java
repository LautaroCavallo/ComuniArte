package com.uade.tpo.comuniarte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication(scanBasePackages = {"com.uade.tpo.comuniarte", "com.uade.tpo.marketplace"})
@EnableMongoRepositories(basePackages = "com.uade.tpo.marketplace.repository.mongodb")
@EnableNeo4jRepositories(basePackages = "com.uade.tpo.marketplace.repository.neo4j")
public class ComuniArteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComuniArteApplication.class, args);
	}

}

