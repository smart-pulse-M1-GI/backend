package com.smartpulse.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/device")
@Slf4j // Génère automatiquement un logger nommé 'log'
public class CardiacTestController {

    /**
     * Endpoint pour recevoir les données du capteur.
     * Le dispositif doit envoyer un POST avec le Header "Content-Type: application/json"
     */
    @PostMapping("/data")
    public ResponseEntity<String> receiveSensorData(@RequestBody Map<String, Object> payload) {

        // Log du contenu complet du JSON reçu
        log.info("Données reçues du dispositif : {}", payload);

        // Optionnel : Accéder à une clé spécifique pour un log plus détaillé
        // if (payload.containsKey("bpm")) {
        //    log.info("Rythme cardiaque détecté : {} BPM", payload.get("bpm"));
        // }

        // Réponse envoyée au dispositif pour confirmer la réception
        return ResponseEntity.ok("Données bien reçues par le serveur");
    }
}