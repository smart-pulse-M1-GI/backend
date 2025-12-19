package com.smartpulse.demo.model.DTO;

import java.time.LocalDate;

public record UserProfileResponse(
        Long UserId,
        String mail,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String role, // "MEDECIN" ou "PATIENT"
        String specialite, // null si patient
        Long medecinId,     // null si médecin ou pas assigné
        Long id     //Id provenant de l'entité Medecin ou Patient
) {}

