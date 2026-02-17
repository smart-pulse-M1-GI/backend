package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CardiacSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patientId;
    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity; // Optionnel, peut être null si mesure libre

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<HeartRateRecord> records;
}