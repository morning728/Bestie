package com.morning.notification.job;

import com.morning.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class NotificationJob implements Job {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Long taskId = dataMap.getLong("taskId");
        String taskName = dataMap.getString("taskName");
        String projectName = dataMap.getString("projectName");
        String reminderText = dataMap.getString("reminderText");
        String remindAt = dataMap.getString("remindAt");

        @SuppressWarnings("unchecked")
        List<String> users = (List<String>) dataMap.get("users");

        log.info("Выполняется напоминание для задачи: {} [{}], проект: {}", taskName, taskId, projectName);

        if (users == null || users.isEmpty()) {
            log.warn("Нет пользователей для отправки напоминания о задаче ID={}", taskId);
            return;
        }

        try {
            notificationService.sendTaskReminder(users, taskName, projectName, reminderText);
            log.info("Успешно отправлены напоминания о задаче ID={}", taskId);
        } catch (Exception e) {
            log.error("Ошибка при отправке напоминания о задаче ID={}: {}", taskId, e.getMessage(), e);
            throw new JobExecutionException(e);
        }
    }
}
