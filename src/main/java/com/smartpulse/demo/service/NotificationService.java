package com.smartpulse.demo.service;

import com.smartpulse.demo.model.entity.Notification;
import com.smartpulse.demo.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository nr, SimpMessagingTemplate smt) {
        this.notificationRepository = nr;
        this.messagingTemplate = smt;
    }

    public void notifyUser(String userId, String message) {
        // 1. Persistance en BD
        Notification notif = new Notification();
        notif.setUserId(userId);
        notif.setMessage(message);
        notif.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notif);

        // 2. Envoi Temps Réel via WebSocket
        // Le client s'abonne à /topic/notifications/{userId}
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);
    }
}