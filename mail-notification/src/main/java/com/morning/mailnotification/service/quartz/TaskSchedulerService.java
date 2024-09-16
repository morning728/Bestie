package com.morning.mailnotification.service.quartz;

import com.morning.mailnotification.job.NotificationJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskSchedulerService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleTask(Map<String, String> data) throws SchedulerException {
        Long taskId = Long.valueOf(data.get("taskId"));
        String taskName = data.get("taskName");
        LocalDateTime deadline = LocalDateTime.parse(data.get("deadline"));
        List<String> users = List.of(data.get("users").split(","));
        scheduleTask(taskId, taskName, users, deadline);
    }

    public void scheduleTask(Long taskId, String taskName, List<String> users, LocalDateTime deadline) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("notifyTask", taskId.toString())
                .usingJobData("taskId", taskId)
                .usingJobData("taskName", taskName)
                .usingJobData("deadline", String.valueOf(deadline))
                .build();

        jobDetail.getJobDataMap().put("users", users);

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("notifyTrigger", taskId.toString())
                .startAt(Date.from(deadline.minusMinutes(2879).atZone(ZoneId.systemDefault()).toInstant()))  // Устанавливаем время дедлайна
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void rescheduleTask(Long taskId,String taskName, List<String> users, LocalDateTime newDeadline) throws SchedulerException {
        JobKey jobKey = new JobKey("notifyTask", taskId.toString());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey); // Удаляем старую задачу
        }

        // Планируем новую задачу
        scheduleTask(taskId,taskName, users, newDeadline);
    }

    public void deleteScheduledTask(Long taskId) throws SchedulerException {
        JobKey jobKey = new JobKey("notifyTask", taskId.toString());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);  // Удаляем задачу из расписания
        }
    }
}

