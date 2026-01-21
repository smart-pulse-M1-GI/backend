package com.smartpulse.demo.model.DTO;

// Pour démarrer une session liée à une activité
public record StartActivitySessionRequest(String patientId, Long activityId) {}
