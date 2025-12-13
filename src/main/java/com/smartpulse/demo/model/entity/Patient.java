package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

// Patient.java
@Entity
@Getter @Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;
}
