package com.smartpulse.demo.repository;

import com.smartpulse.demo.model.entity.Seuil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SeuilRepository extends JpaRepository<Seuil, Long> {
    Optional<Seuil> findByPatientId(Long patientId);
}