package com.morning.notification.service.quartz;

import com.morning.notification.job.NotificationJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskSchedulerService {

    @Autowired
    private Scheduler scheduler;

    @Data
    @AllArgsConstructor
    private class ParsedTask{
        private Long taskId;
        private String taskName;
        private LocalDateTime deadline;
        private List<String> users;
    }

    private ParsedTask parseTask(Map<String, String> data){
        return new ParsedTask(
                Long.valueOf(data.get("taskId")),
                data.get("taskName"),
                LocalDateTime.parse(data.get("deadline")),
                List.of(data.get("users").split(",")));
    }

    public void scheduleTask(Map<String, String> data) throws SchedulerException {
        ParsedTask parsedTask = parseTask(data);
        if ( Duration.between(LocalDateTime.now(), parsedTask.getDeadline()).toMinutes() > 1435) {
            JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                    .withIdentity("notifyTask", parsedTask.getTaskId().toString())
                    .usingJobData("taskId", parsedTask.getTaskId())
                    .usingJobData("taskName", parsedTask.getTaskName())
                    .usingJobData("deadline", String.valueOf(parsedTask.getDeadline()))
                    .build();

            jobDetail.getJobDataMap().put("users", parsedTask.getUsers());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("notifyTrigger", parsedTask.getTaskId().toString())
                    .startAt(Date.from(parsedTask.getDeadline().minusHours(24).atZone(ZoneId.systemDefault()).toInstant()))  // Устанавливаем время дедлайна
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    public void rescheduleTask(Map<String, String> data) throws SchedulerException {
        ParsedTask parsedTask = parseTask(data);

        JobKey jobKey = new JobKey("notifyTask", parsedTask.getTaskId().toString());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey); // Удаляем старую задачу
        }

        // Планируем новую задачу
        scheduleTask(data);
    }

    public void deleteScheduledTask(Map<String, String> data) throws SchedulerException {
        JobKey jobKey = new JobKey("notifyTask", data.get("taskId").toString());

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);  // Удаляем задачу из расписания
        }
    }
}

