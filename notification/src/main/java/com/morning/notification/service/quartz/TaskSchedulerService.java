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
    private static class ParsedReminder {
        private Long taskId;
        private String taskName;
        private String projectName;
        private String reminderText;
        private LocalDateTime remindAt;
        private List<String> users;
    }

    private ParsedReminder parseReminder(Map<String, String> data) {
        return new ParsedReminder(
                Long.parseLong(data.get("taskId")),
                data.get("taskName"),
                data.get("projectName"),
                data.get("reminderText"),
                LocalDateTime.parse(data.get("remindAt")),
                List.of(data.get("users").split(","))
        );
    }

    public void scheduleReminder(Map<String, String> data) throws SchedulerException {
        ParsedReminder reminder = parseReminder(data);

        if (reminder.getRemindAt().isBefore(LocalDateTime.now())) {
            log.warn("Время напоминания уже прошло для задачи ID={}", reminder.getTaskId());
            return;
        }

        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(buildJobKey(reminder.getTaskId()))
                .usingJobData("taskId", reminder.getTaskId())
                .usingJobData("taskName", reminder.getTaskName())
                .usingJobData("reminderText", reminder.getReminderText())
                .usingJobData("projectName", reminder.getProjectName())
                .usingJobData("remindAt", reminder.getRemindAt().toString())
                .build();

        jobDetail.getJobDataMap().put("users", reminder.getUsers());

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(buildTriggerKey(reminder.getTaskId()))
                .startAt(Date.from(reminder.getRemindAt().atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("Запланировано напоминание для задачи ID={} на {}", reminder.getTaskId(), reminder.getRemindAt());
    }

    public void rescheduleReminder(Map<String, String> data) throws SchedulerException {
        ParsedReminder reminder = parseReminder(data);
        deleteReminder(reminder.getTaskId());
        scheduleReminder(data);
    }

    public void deleteReminder(Long taskId) throws SchedulerException {
        JobKey jobKey = buildJobKey(taskId);

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("Удалено напоминание для задачи ID={}", taskId);
        } else {
            log.warn("Попытка удалить несуществующее напоминание для задачи ID={}", taskId);
        }
    }

    private JobKey buildJobKey(Long taskId) {
        return JobKey.jobKey("notifyTask_" + taskId);
    }

    private TriggerKey buildTriggerKey(Long taskId) {
        return TriggerKey.triggerKey("notifyTrigger_" + taskId);
    }
}
