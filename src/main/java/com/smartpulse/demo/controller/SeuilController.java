package com.smartpulse.demo.controller;

import com.smartpulse.demo.model.DTO.SeuilResponse;
import com.smartpulse.demo.model.entity.Patient;
import com.smartpulse.demo.model.entity.Seuil;
import com.smartpulse.demo.repository.PatientRepository;
import com.smartpulse.demo.repository.SeuilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/thresholds")
@RequiredArgsConstructor
public class SeuilController {

    private final SeuilRepository seuilRepository;
    private final PatientRepository patientRepository;

    // Définir ou mettre à jour les seuils d'un patient
    @PostMapping("/set/{patientId}")
    public ResponseEntity<SeuilResponse> setThresholds(@PathVariable Long patientId, @RequestParam int min, @RequestParam int max) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        Seuil seuil = seuilRepository.findByPatientId(patientId).orElse(new Seuil());
        seuil.setPatient(patient);
        seuil.setBpmMin(min);
        seuil.setBpmMax(max);

        Seuil savedSeuil = seuilRepository.save(seuil);

        SeuilResponse dto = new SeuilResponse(
                savedSeuil.getId(),
                savedSeuil.getBpmMin(),
                savedSeuil.getBpmMax(),
                patient.getId(),
                patient.getPrenom() + " " + patient.getNom()
        );

        return ResponseEntity.ok(dto);
    }

    // Récupérer les seuils d'un patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<SeuilResponse> getThresholds(@PathVariable Long patientId) {
        return seuilRepository.findByPatientId(patientId)
                .map(
                        seuil -> {
                            return new SeuilResponse(
                                    seuil.getId(),
                                    seuil.getBpmMin(),
                                    seuil.getBpmMax(),
                                    seuil.getPatient().getId(),
                                    seuil.getPatient().getPrenom() + " " + seuil.getPatient().getNom()
                            );
                        }
                ).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}