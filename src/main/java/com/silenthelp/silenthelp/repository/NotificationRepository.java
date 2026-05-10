package com.silenthelp.silenthelp.repository;

import com.silenthelp.silenthelp.model.Notification;
import com.silenthelp.silenthelp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadStatusFalse(User user);
}
