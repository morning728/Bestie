package com.morning.mailnotification;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class MailNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailNotificationApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate){
//		return args -> {
//			for (int i = 0; i < 100; i++) {
//				kafkaTemplate.send("notification", "data" + i);
//			}
//		};
//	}
}
