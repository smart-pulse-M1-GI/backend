package com.smartpulse.demo.model.DTO;

import java.time.LocalDate;
import java.util.Date;

public record RegisterMedecinRequest(
        String mail,
        String password,
        String nom,
        String prenom,
        LocalDate dateNaissance,
        String specialite
) {}
