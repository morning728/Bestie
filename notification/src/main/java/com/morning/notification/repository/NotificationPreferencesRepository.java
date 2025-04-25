package com.morning.notification.repository;

import com.morning.notification.entity.user.Contacts;
import com.morning.notification.entity.user.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    Optional<NotificationPreferences> findByUsername(String username);
    @Query(value =  "SELECT username, telegram_id, email, chat_id from public.notification_preferences where username = :username", nativeQuery = true)
    Optional<Contacts> findContactsByUsername(String username);
}
