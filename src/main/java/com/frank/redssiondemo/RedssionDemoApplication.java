package com.frank.redssiondemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.frank.redssiondemo.dao.mapper")
public class RedssionDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedssionDemoApplication.class, args);
	}

}
