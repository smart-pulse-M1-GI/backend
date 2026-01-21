package com.smartpulse.demo.repository;

import com.smartpulse.demo.model.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Trouver toutes les activités d'un patient (ordonnées par les plus récentes)
    List<Activity> findByPatientIdOrderByIdDesc(String patientId);

    // Trouver les activités créées par un médecin spécifique
    List<Activity> findByDoctorId(String doctorId);

    // Trouver uniquement les activités non terminées d'un patient
    List<Activity> findByPatientIdAndCompletedFalse(String patientId);
}