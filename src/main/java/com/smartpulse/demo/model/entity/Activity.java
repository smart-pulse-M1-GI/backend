package com.smartpulse.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Getter @Setter @NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String patientId;
    private String doctorId;
    private boolean completed = false;
    private int durationInMinutes;

    @OneToMany(mappedBy = "activity")
    @JsonIgnore 
    private List<CardiacSession> sessions;
}

