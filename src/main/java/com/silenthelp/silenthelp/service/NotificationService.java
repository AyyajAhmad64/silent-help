package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.model.Notification;
import com.silenthelp.silenthelp.model.RoleName;
import com.silenthelp.silenthelp.model.User;
import com.silenthelp.silenthelp.repository.NotificationRepository;
import com.silenthelp.silenthelp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void notify(User user, String title, String message, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLink(link);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyAdmins(String title, String message, String link) {
        userRepository.findDistinctByRolesName(RoleName.ADMIN).stream()
                .filter(User::isEnabled)
                .filter(user -> !user.isDeleted())
                .forEach(admin -> notify(admin, title, message, link));
    }

    @Transactional(readOnly = true)
    public List<Notification> forUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        return notificationRepository.countByUserAndReadStatusFalse(user);
    }

    @Transactional
    public void markRead(Long id, User user) {
        notificationRepository.findById(id)
                .filter(notification -> notification.getUser().getId().equals(user.getId()))
                .ifPresent(notification -> notification.setReadStatus(true));
    }

    @Transactional
    public String openAndMarkRead(Long id, User user) {
        return notificationRepository.findById(id)
                .filter(notification -> notification.getUser().getId().equals(user.getId()))
                .map(notification -> {
                    notification.setReadStatus(true);
                    String link = notification.getLink();
                    return link == null || link.isBlank() ? "/notifications" : link;
                })
                .orElse("/notifications");
    }
}
