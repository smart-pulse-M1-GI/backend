package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Seuil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int bpmMin;
    private int bpmMax;

    @OneToOne
    @JoinColumn(name = "patient_id", unique = true)
    private Patient patient;
}