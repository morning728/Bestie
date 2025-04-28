package com.morning.notification.service.quartz;

import com.morning.notification.entity.task.TaskNotificationEvent;
import com.morning.notification.entity.user.ParsedReminder;
import com.morning.notification.job.NotificationJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskSchedulerService {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Scheduler scheduler;

    private ParsedReminder parseReminder(Map<String, String> data) {
        String usernamesStr = data.getOrDefault("usernames", "");
        List<String> usernames = usernamesStr.isEmpty()
                ? List.of()
                : Arrays.stream(usernamesStr.split(","))
                .map(String::trim)
                .toList();


        return new ParsedReminder(
                Long.parseLong(data.get("taskId")),
                data.get("taskName"),
                data.get("projectName"),
                data.get("reminderText"),
                LocalDateTime.parse(data.get("remindAt")),
                usernames
        );
    }


    public void scheduleReminder(Map<String, String> data) throws SchedulerException {
        ParsedReminder reminder = parseReminder(data);

        if (reminder.getRemindAt().isBefore(LocalDateTime.now())) {
            log.warn("Время напоминания уже прошло для задачи ID={}", reminder.getTaskId());
            return;
        }

        JobKey jobKey = buildJobKey(reminder.getTaskId());
        TriggerKey triggerKey = buildTriggerKey(reminder.getTaskId());

        if (scheduler.checkExists(jobKey)) {
            log.info("⏳ Найдено существующее напоминание для задачи ID={}, перезаписываем...", reminder.getTaskId());
            scheduler.deleteJob(jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(jobKey)
                .usingJobData("taskId", reminder.getTaskId())
                .usingJobData("taskName", reminder.getTaskName())
                .usingJobData("reminderText", reminder.getReminderText())
                .usingJobData("projectName", reminder.getProjectName())
                .usingJobData("remindAt", reminder.getRemindAt().toString())
                .build();

        jobDetail.getJobDataMap().put("usernames", reminder.getUsernames());

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(Date.from(reminder.getRemindAt().atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("✅ Запланировано новое напоминание для задачи ID={} на {}", reminder.getTaskId(), reminder.getRemindAt());
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

    public void createTaskReminder(TaskNotificationEvent event) {
        if (event.getUsernames() == null || event.getUsernames().isBlank()) {
            log.warn("Получено пустое напоминание для задачи ID={}", event.getTaskId());
            deleteTaskReminder(event);
            return;
        }

        Map<String, String> data = Map.of(
                "taskId", event.getTaskId().toString(),
                "taskName", event.getTaskTitle(),
                "projectName", event.getProjectTitle(),
                "reminderText", event.getReminderText() != null ? event.getReminderText() : "",
                "remindAt", event.getRemindAt().toString(),
                "usernames", event.getUsernames()
        );

        try {
            scheduleReminder(data);
        } catch (SchedulerException e) {
            log.error("Ошибка при планировании напоминания для задачи ID={}", event.getTaskId(), e);
        }
    }

    public void deleteTaskReminder(TaskNotificationEvent event) {
        try {
            deleteReminder(event.getTaskId());
        } catch (SchedulerException e) {
            log.error("Ошибка при удалении напоминания для задачи ID={}", event.getTaskId(), e);
        }
    }


    private JobKey buildJobKey(Long taskId) {
        return JobKey.jobKey("notifyTask_" + taskId);
    }

    private TriggerKey buildTriggerKey(Long taskId) {
        return TriggerKey.triggerKey("notifyTrigger_" + taskId);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Каждый день в 3:00 ночи
    public void cleanUpCompletedReminders() {
        String deleteFiredTriggers = "DELETE FROM qrtz_fired_triggers";
        String deleteSimpleTriggers = "DELETE FROM qrtz_simple_triggers WHERE trigger_name IN (SELECT trigger_name FROM qrtz_triggers WHERE trigger_state IN ('COMPLETE', 'ERROR', 'DELETED'))";
        String deleteCronTriggers = "DELETE FROM qrtz_cron_triggers WHERE trigger_name IN (SELECT trigger_name FROM qrtz_triggers WHERE trigger_state IN ('COMPLETE', 'ERROR', 'DELETED'))";
        String deleteTriggers = "DELETE FROM qrtz_triggers WHERE trigger_state IN ('COMPLETE', 'ERROR', 'DELETED')";
        String deleteJobDetails = "DELETE FROM qrtz_job_details WHERE job_name NOT IN (SELECT job_name FROM qrtz_triggers)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Одна транзакция

            try (PreparedStatement ps1 = conn.prepareStatement(deleteFiredTriggers);
                 PreparedStatement ps2 = conn.prepareStatement(deleteSimpleTriggers);
                 PreparedStatement ps3 = conn.prepareStatement(deleteCronTriggers);
                 PreparedStatement ps4 = conn.prepareStatement(deleteTriggers);
                 PreparedStatement ps5 = conn.prepareStatement(deleteJobDetails)) {

                ps1.executeUpdate();
                ps2.executeUpdate();
                ps3.executeUpdate();
                ps4.executeUpdate();
                ps5.executeUpdate();

                conn.commit();
                log.info("✅ Успешно очищены старые напоминания из Quartz.");
            } catch (SQLException e) {
                conn.rollback();
                log.error("❌ Ошибка при очистке напоминаний в Quartz: {}", e.getMessage(), e);
            }
        } catch (SQLException e) {
            log.error("❌ Ошибка при подключении к базе данных для очистки напоминаний: {}", e.getMessage(), e);
        }
    }

}
