package com.smartpulse.demo.model.DTO;
import java.time.LocalDateTime;

public record NotificationResponse(Long id, String message, LocalDateTime createdAt, boolean isRead) {}