package com.morning.taskapimain.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic participantsEditTopic(){
        return TopicBuilder.name("participants-edit-topic")
                .build();
    }

    @Bean
    public NewTopic taskNotificationTopic(){
        return TopicBuilder.name("task-notification-topic")
                .build();
    }
}
