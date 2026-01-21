package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;       // ex: "Après le repas"
    private String description;
    private String patientId;
    private String doctorId;
    private boolean completed = false;
    private int durationInMinutes; // Durée prévue pour la mesure

    @OneToMany(mappedBy = "activity")
    private List<CardiacSession> sessions;
}

