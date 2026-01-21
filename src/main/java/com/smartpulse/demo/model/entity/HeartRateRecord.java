package com.smartpulse.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HeartRateRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int bpm;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore // Empêche de renvoyer toute la session pour chaque point de donnée
    private CardiacSession session;
}