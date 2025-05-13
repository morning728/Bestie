package com.morning.security.emailSender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();


    public void sendMailNotification(String username, String email) {
        try{
            Map<String, String> event = new HashMap<>();
            event.put("username", username);
            event.put("email", email);
            event.put("action", "VERIFY_EMAIL");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("mail-verification-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
        }
    }

    public void sendTelegramDataChange(String username, String telegramId, Long chatId) {
        try{
            Map<String, String> event = new HashMap<>();
            event.put("username", username);
            event.put("telegramId", telegramId);
            event.put("chatId", chatId.toString());
            event.put("action", "CHANGE_TELEGRAM_DATA");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("tech-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
        }
    }

    public void sendUsernameToNotificationService(String username) {
        try{
            Map<String, String> event = new HashMap<>();
            event.put("username", username);
            event.put("action", "REGISTER");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("tech-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
        }
    }
}
