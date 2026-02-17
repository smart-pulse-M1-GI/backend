package com.smartpulse.demo.model.DTO;

import java.time.LocalDate;

public record RegisterPatientRequest(
        String mail,
        String password,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        Long medecinId  // optionnel : null si pas encore assigné
) {}
