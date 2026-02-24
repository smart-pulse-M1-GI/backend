package com.smartpulse.demo.model.DTO;

import java.time.LocalDate;

public record UpdateProfileRequest(
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String specialite, // null si patient
        String password  // optionnel - null si on ne veut pas changer le mot de passe
) {}

