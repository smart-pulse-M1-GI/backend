package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;      // Destinataire (Patient ou Médecin)
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead = false;
}
