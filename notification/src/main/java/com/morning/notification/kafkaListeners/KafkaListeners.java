package com.morning.notification.kafkaListeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morning.notification.entity.project.DeleteEvent;
import com.morning.notification.entity.project.InviteEvent;
import com.morning.notification.entity.task.TaskNotificationEvent;
import com.morning.notification.entity.user.TelegramDataEvent;
import com.morning.notification.service.NotificationService;
import com.morning.notification.service.UserService;
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
    private final NotificationService notificationService;
    private final UserService userService;
    private final TaskSchedulerService taskSchedulerService;
    private ObjectMapper objectMapper = new ObjectMapper();

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
            topics = "tech-topic",
            groupId = "tech"
    )
    void techListener(String data) {
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch (event.get("action")) {
                case "CHANGE_TELEGRAM_DATA": {
                    userService.updateTelegramData(objectMapper.readValue(data, TelegramDataEvent.class));
                    break;
                }
                case "REGISTER": {
                    userService.addUser(event.get("username"));
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    @KafkaListener(
            topics = "participants-edit-topic",
            groupId = "participants-edit"
    )
    void editorParticipantsListener(String data) {
        Map<String, String> event = null;
        try {
            event = objectMapper.readValue(data, Map.class);
            switch (event.get("action")) {
                case "INVITE_TO_PROJECT": {
                    notificationService.sendInviteToProject(objectMapper.readValue(data, InviteEvent.class));
                    break;
                }
                case "DELETE_FROM_PROJECT": {
                    notificationService.sendDeleteNotification(objectMapper.readValue(data, DeleteEvent.class));
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @KafkaListener(
            topics = "task-notification-topic",
            groupId = "tasks-edit"
    )
    public void taskUpdatesListener(String data) {
        try {
            TaskNotificationEvent event = objectMapper.readValue(data, TaskNotificationEvent.class);
            switch (event.getAction()) {
                case "ASSIGNED_TO_TASK" -> notificationService.sendTaskAssignmentNotification(event);
                case "STATUS_CHANGE" -> notificationService.sendStatusChangeNotification(event);
                case "CREATE_REMINDER" -> taskSchedulerService.createTaskReminder(event);
                case "DELETE_REMINDER" -> taskSchedulerService.deleteTaskReminder(event);
                default -> log.warn("Неизвестный action в task-updates-topic: {}", event.getAction());
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке Kafka-сообщения в task-updates-topic", e);
        }
    }



/*    @KafkaListener(
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
    }*/


//    @KafkaListener(
//            topics = "notification",
//            groupId = "confirmation1"
//    )
//    void listener2(String data){
//        System.out.println(data + "recieved");
//    }
}
