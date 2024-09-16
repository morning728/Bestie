package com.morning.taskapimain.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.exception.KafkaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Void> sendInviteToProject(Long projectId, String fromUsername, String toUsername, String toEmail){
        try{
            Map<String, String> event = new HashMap<>();
            event.put("username", toUsername);
            event.put("email", toEmail);
            event.put("from", fromUsername);
            event.put("projectId", String.valueOf(projectId));
            event.put("action", "INVITE_TO_PROJECT");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("participants-edit-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
            return Mono.error(new KafkaException("ERROR: Kafka cant do it right now"));
        }
        return Mono.empty();
    }

    public Mono<Void> sendDeleteNotification(String projectName, String fromUsername, String toUsername, String toEmail){
        try{
            Map<String, String> event = new HashMap<>();
            event.put("username", toUsername);
            event.put("email", toEmail);
            event.put("from", fromUsername);
            event.put("projectName", projectName);
            event.put("action", "DELETE_FROM_PROJECT");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("participants-edit-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
            return Mono.error(new KafkaException("ERROR: Kafka cant do it right now"));
        }
        return Mono.empty();
    }

    public Mono<Void> sendTaskCreation(Long taskId, String taskName, List<String> emails, LocalDateTime deadline){
        try{
            Map<String, String> event = new HashMap<>();
            event.put("taskId", String.valueOf(taskId));
            event.put("taskName", taskName);
            event.put("deadline", deadline.toString());
            event.put("users", String.join(",", emails));
            event.put("action", "TASK_CREATION");
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("mail-notification-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
            return Mono.error(new KafkaException("ERROR: Kafka cant do it right now"));
        }
        return Mono.empty();
    }
}
