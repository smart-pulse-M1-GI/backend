package com.smartpulse.demo.controller;

import com.smartpulse.demo.model.entity.Notification;
import com.smartpulse.demo.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // 1. Récupérer toutes les notifications d'un utilisateur (Patient ou Médecin)
    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(@PathVariable String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 2. Récupérer uniquement les notifications non lues (pour le badge sur la cloche)
    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable String userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // 3. Marquer une notification spécifique comme lue
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(notif -> {
                    notif.setRead(true);
                    notificationRepository.save(notif);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Tout marquer comme lu (quand on ouvre le panneau de notifications)
    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable String userId) {
        notificationRepository.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}