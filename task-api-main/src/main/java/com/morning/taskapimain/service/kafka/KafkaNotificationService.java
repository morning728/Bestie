package com.morning.taskapimain.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.taskapimain.entity.kafka.project.DeleteEvent;
import com.morning.taskapimain.entity.kafka.project.InviteEvent;
import com.morning.taskapimain.entity.kafka.task.TaskNotificationEvent;

import com.morning.taskapimain.exception.KafkaException;
import com.morning.taskapimain.repository.ProjectRepository;
import com.morning.taskapimain.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final ProjectRepository projectRepository;

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

    public Mono<Void> sendDeleteFromProject(DeleteEvent deleteEvent) {
        try {
            kafkaTemplate.send(
                    new ProducerRecord<>("participants-edit-topic", objectMapper.writeValueAsString(deleteEvent))
            );
        } catch (JsonProcessingException e) {
            log.error("Kafka exception: " + e.getMessage());
            return Mono.error(new KafkaException("ERROR: Kafka can't send delete event right now"));
        }
        return Mono.empty();
    }


    public Mono<Void> sendTaskNotification(TaskNotificationEvent event) {
        try {
            kafkaTemplate.send(new ProducerRecord<>("task-notification-topic", objectMapper.writeValueAsString(event)));
        } catch (JsonProcessingException e) {
            log.error("Kafka error while sending task notification: {}", e.getMessage());
            return Mono.error(new KafkaException("Ошибка Kafka при отправке уведомления"));
        }
        return Mono.empty();
    }

}
