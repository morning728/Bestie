package com.morning.mailnotification.job;

import com.morning.mailnotification.service.MailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationJob implements Job {

    @Autowired
    private MailService mailService; // Ваш сервис отправки email

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskName = context.getJobDetail().getJobDataMap().getString("taskName");
        String deadline = context.getJobDetail().getJobDataMap().getString("deadline");
        List<String> users = (List<String>) context.getJobDetail().getJobDataMap().get("users");

        // Логика отправки уведомлений
        mailService.sendTaskDeadlineReminder(users, taskName, deadline);
    }
}

