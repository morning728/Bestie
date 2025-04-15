package com.morning.notification.kafkaListeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.notification.entity.project.DeleteEvent;
import com.morning.notification.entity.project.InviteEvent;
import com.morning.notification.service.NotificationService;
import com.morning.notification.service.quartz.TaskSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;
    private final TaskSchedulerService taskSchedulerService;


/*    @KafkaListener(
            topics = "mail-verification-topic",
            groupId = "confirmation"
    )
    void listener(String data){
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch(event.get("action")){
                case "VERIFY_EMAIL":{
                    notificationService.sendVerificationMail(event);
                    break;
                }
            }
        } catch(Exception e){
            log.error(e.toString());
        }
    }*/


    @KafkaListener(
            topics = "participants-edit-topic",
            groupId = "participants-edit"
    )
    void editorParticipantsListener(String data){
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch(event.get("action")){
                case "INVITE_TO_PROJECT":{
                    notificationService.sendInviteToProject(objectMapper.readValue(data, InviteEvent.class));
                    break;
                }
                case "DELETE_FROM_PROJECT":{
                    notificationService.sendDeleteNotification(objectMapper.readValue(data, DeleteEvent.class));
                    break;
                }
            }
        } catch(Exception e){
            log.error(e.toString());
        }
    }

    @KafkaListener(
            topics = "task-notification-topic",
            groupId = "tasks-edit"
    )
    public void taskUpdatesListener(String data) {
        try {
            Map<String, String> eventMap = objectMapper.readValue(data, Map.class);
            String action = eventMap.get("action");

            switch (action) {
                case "ASSIGNED_TO_TASK" -> {
                    TaskNotificationEvent event = objectMapper.readValue(data, TaskNotificationEvent.class);
                    notificationService.sendTaskAssignmentNotification(event);
                }
                case "TASK_UPDATED" -> {
                    TaskNotificationEvent event = objectMapper.readValue(data, TaskNotificationEvent.class);
                    notificationService.sendTaskUpdateNotification(event);
                }
                case "TASK_DEADLINE_SOON" -> {
                    TaskNotificationEvent event = objectMapper.readValue(data, TaskNotificationEvent.class);
                    notificationService.sendTaskDeadlineReminder(event);
                }
                // Добавляй другие case по мере расширения
                default -> log.warn("Неизвестный action в task-updates-topic: {}", action);
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке Kafka-сообщения в task-updates-topic", e);
        }
    }



    @KafkaListener(
            topics = "task-notification-topic",
            groupId = "task"
    )
    void taskEEditListener(String data){
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch(event.get("action")){
                case "TASK_CREATE":{
                    taskSchedulerService.scheduleTask(event);
                    break;
                }
                case "TASK_UPDATE":{
                    log.info("AAADASDSAD");
                    taskSchedulerService.rescheduleTask(event);
                    break;
                }
                case "TASK_DELETE":{
                    taskSchedulerService.deleteScheduledTask(event);
                    break;
                }
            }
        } catch(Exception e){
            log.error(e.toString());
        }
    }


//    @KafkaListener(
//            topics = "notification",
//            groupId = "confirmation1"
//    )
//    void listener2(String data){
//        System.out.println(data + "recieved");
//    }
}
