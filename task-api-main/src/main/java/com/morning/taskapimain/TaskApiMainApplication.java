package com.morning.taskapimain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaskApiMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskApiMainApplication.class, args);
	}

}
