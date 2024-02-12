package com.frank.redissondemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.frank.redissondemo.dao.mapper")
public class RedissonDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedissonDemoApplication.class, args);
	}

}
