package org.jozif.demo4mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.jozif.demo4mybatis.dao")
public class Demo4mybatisApplication {

	public static void main(String[] args) {
		SpringApplication.run(Demo4mybatisApplication.class, args);
	}
}
