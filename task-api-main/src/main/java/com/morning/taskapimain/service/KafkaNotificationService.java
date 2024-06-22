package com.morning.taskapimain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationService {

    private final KafkaTemplate kafkaTemplate;

//    public void sendEmailVerification()
}
