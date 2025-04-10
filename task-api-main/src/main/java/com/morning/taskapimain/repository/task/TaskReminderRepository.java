package com.morning.taskapimain.repository.task;

import com.morning.taskapimain.entity.task.TaskReminder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

public interface TaskReminderRepository extends R2dbcRepository<TaskReminder, Long> {
    // 🔹 Получение напоминания по ID
    @Query("SELECT * FROM task_reminder WHERE id = :reminderId")
    Mono<TaskReminder> findReminderById(@Param("reminderId") Long reminderId);

    // 🔹 Удаление напоминания
    @Query("DELETE FROM task_reminder WHERE id = :reminderId")
    Mono<Void> deleteReminderById(@Param("reminderId") Long reminderId);

    // 🔹 Удаление напоминания
    @Query("DELETE FROM task_reminder WHERE task_id = :taskId")
    Mono<Void> deleteReminderByTaskId(@Param("taskId") Long taskId);

    // 🔹 Обновление напоминания
    @Query("UPDATE task_reminder SET reminder_date = :newDate, reminder_time = :newTime WHERE id = :reminderId")
    Mono<Void> updateReminder(@Param("reminderId") Long reminderId,
                              @Param("newDate") LocalDate newDate,
                              @Param("newTime") LocalTime newTime);

    @Query("SELECT * FROM task_reminder WHERE task_id = :taskId")
    Mono<TaskReminder> findByTaskId(@Param("taskId") Long taskId);
    // 🔹 Добавление напоминания к задаче
    @Query("INSERT INTO task_reminder (task_id, reminder_date, reminder_time) VALUES (:taskId, :reminderDate, :reminderTime) RETURNING *")
    Mono<TaskReminder> addReminder(@Param("taskId") Long taskId, @Param("reminderDate") LocalDate reminderDate, @Param("reminderTime") LocalTime reminderTime);

    // 🔹 Удаление всех напоминаний задачи
    @Query("DELETE FROM task_reminder WHERE task_id = :taskId")
    Mono<Void> deleteReminders(@Param("taskId") Long taskId);
}
