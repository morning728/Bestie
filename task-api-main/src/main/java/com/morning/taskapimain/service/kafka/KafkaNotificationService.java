package com.morning.taskapimain.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.kafka.InviteEvent;
import com.morning.taskapimain.exception.BadRequestException;
import com.morning.taskapimain.exception.KafkaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
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

    public Mono<Void> sendInviteToProject(InviteEvent inviteEvent){
        try{
            kafkaTemplate.send(
                    new ProducerRecord<>("participants-edit-topic", objectMapper.writeValueAsString(inviteEvent))
            );
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

    public Mono<Void> sendTaskAction(Long taskId, @Nullable String taskName, @Nullable List<String> emails, @Nullable LocalDateTime deadline, String ACTION){
        try{
            Map<String, String> event = new HashMap<>();
            if(ACTION.equals("TASK_CREATE") || ACTION.equals("TASK_UPDATE")) {
                event.put("taskId", String.valueOf(taskId));
                event.put("taskName", taskName);
                event.put("deadline", deadline.toString());
                event.put("users", String.join(",", emails));
                event.put("action", ACTION);
            } else if (ACTION.equals("TASK_DELETE")) {
                event.put("taskId", String.valueOf(taskId));
                event.put("action", ACTION);
            } else {
                return Mono.error(new BadRequestException("ERROR: Action is invalid!"));
            }

            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(new ProducerRecord<>("mail-notification-topic", eventAsString));
        } catch (JsonProcessingException e){
            log.error("Kafka exception: " + e.toString());
            return Mono.error(new KafkaException("ERROR: Kafka cant do it right now"));
        }
        return Mono.empty();
    }

    public Mono<Void> sendTaskCreate(Long taskId, String taskName, List<String> emails, LocalDateTime deadline){
        return sendTaskAction(taskId, taskName, emails, deadline, "TASK_CREATE");
    }

    public Mono<Void> sendTaskUpdate(Long taskId, String taskName, List<String> emails, LocalDateTime deadline){
        return sendTaskAction(taskId, taskName, emails, deadline, "TASK_UPDATE");
    }

    public Mono<Void> sendTaskDelete(Long taskId){
        return sendTaskAction(taskId, null, null, null, "TASK_DELETE");
    }
}
