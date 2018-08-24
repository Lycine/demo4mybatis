package org.jozif.articleFetcher;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.jozif.articleFetcher.dao")
public class ArticleFetcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleFetcherApplication.class, args);
	}
}
