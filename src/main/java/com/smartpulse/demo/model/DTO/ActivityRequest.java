package com.smartpulse.demo.model.DTO;

public record ActivityRequest(String title, String description, String patientId, String doctorId, int durationInMinutes) {}
