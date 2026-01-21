package com.smartpulse.demo.model.DTO;

public record SeuilResponse(
         Long id,
         int bpmMin,
         int bpmMax,
         Long patientId,
        String patientNomComplet
) {}
