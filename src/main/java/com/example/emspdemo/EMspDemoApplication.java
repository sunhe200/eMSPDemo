package com.example.emspdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.emspdemo.repository")
public class EMspDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EMspDemoApplication.class, args);
	}

}
