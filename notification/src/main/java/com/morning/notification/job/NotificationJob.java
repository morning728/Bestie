package com.morning.notification.job;

import com.morning.notification.service.NotificationService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationJob implements Job {

    @Autowired
    private NotificationService notificationService; // Ваш сервис отправки email

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskName = context.getJobDetail().getJobDataMap().getString("taskName");
        String deadline = context.getJobDetail().getJobDataMap().getString("deadline");
        List<String> users = (List<String>) context.getJobDetail().getJobDataMap().get("users");

        // Логика отправки уведомлений
        //notificationService.sendTaskDeadlineReminder(users, taskName, deadline);
    }
}

