package com.morning.openrequesthandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OpenRequestHandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenRequestHandlerApplication.class, args);
	}

}
