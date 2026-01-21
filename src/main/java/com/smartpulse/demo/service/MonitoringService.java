package com.smartpulse.demo.service;

import com.smartpulse.demo.model.entity.Patient;
import com.smartpulse.demo.model.entity.Seuil;
import com.smartpulse.demo.repository.PatientRepository;
import com.smartpulse.demo.repository.SeuilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final SeuilRepository seuilRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    public void checkBpmThresholds(String patientMail, int currentBpm) {
        // 1. Récupérer le patient par son mail (identifiant dans la session)
        patientRepository.findByUserMail(patientMail).ifPresent(patient -> {

            // 2. Chercher les seuils dans la table indépendante
            seuilRepository.findByPatientId(patient.getId()).ifPresent(seuil -> {

                String alertMessage = null;

                if (currentBpm > seuil.getBpmMax()) {
                    alertMessage = String.format("ALERTE : BPM élevé (%d) pour %s %s. (Seuil Max: %d)",
                            currentBpm, patient.getNom(), patient.getPrenom(), seuil.getBpmMax());
                } else if (currentBpm < seuil.getBpmMin()) {
                    alertMessage = String.format("ALERTE : BPM faible (%d) pour %s %s. (Seuil Min: %d)",
                            currentBpm, patient.getNom(), patient.getPrenom(), seuil.getBpmMin());
                }

                // 3. Si une alerte est générée, on notifie le médecin
                if (alertMessage != null && patient.getMedecin() != null) {
                    notificationService.notifyUser(
                            patient.getMedecin().getId().toString(),
                            alertMessage
                    );
                }
            });
        });
    }
}