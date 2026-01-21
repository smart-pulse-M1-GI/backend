package com.smartpulse.demo.repository;

import com.smartpulse.demo.model.entity.CardiacSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<CardiacSession, Long> {
    List<CardiacSession> findByPatientIdOrderByStartTimeDesc(String patientId);
    // Récupérer toutes les sessions de pouls liées à une activité précise
    List<CardiacSession> findByActivityId(Long activityId);

}
