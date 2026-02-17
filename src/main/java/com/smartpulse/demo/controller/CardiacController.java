package com.smartpulse.demo.controller;

import com.smartpulse.demo.config.JwtAuthenticationFilter;
import com.smartpulse.demo.model.DTO.PulseDataDTO;
import com.smartpulse.demo.model.DTO.StartSessionRequest;
import com.smartpulse.demo.model.entity.CardiacSession;
import com.smartpulse.demo.model.entity.HeartRateRecord;
import com.smartpulse.demo.repository.*;
import com.smartpulse.demo.service.MonitoringService;
import com.smartpulse.demo.service.SessionManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cardiac")
public class CardiacController {

    private final SessionRepository sessionRepository;
    private final HeartRateRepository heartRateRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SessionManagerService sessionManager;
    private final MonitoringService monitoringService; // Injection du nouveau service

    private static final Logger logger = LoggerFactory.getLogger(CardiacController.class);

    public CardiacController(SessionRepository sr, HeartRateRepository hr,
                             SimpMessagingTemplate smt, SessionManagerService sm,
                             MonitoringService ms) {
        this.sessionRepository = sr;
        this.heartRateRepository = hr;
        this.messagingTemplate = smt;
        this.sessionManager = sm;
        this.monitoringService = ms;
    }

    @PostMapping("/start")
    public Long startSession(@RequestBody StartSessionRequest request) {
        CardiacSession session = new CardiacSession();
        session.setPatientId(request.patientId());
        session.setStartTime(LocalDateTime.now());
        CardiacSession saved = sessionRepository.save(session);
        sessionManager.setActiveSessionId(saved.getId());
        return saved.getId();
    }

    @PostMapping("/receive")
    public void receiveFromArduino(@RequestBody PulseDataDTO data) {
        logger.debug("Received BPM: {}, Status: {}", data.bpm(), data.status());
        Long activeSessionId = sessionManager.getActiveSessionId();

        if (activeSessionId == null) {
            logger.warn("Données reçues mais aucune session active (temps expiré ?)");
            return;
        }

        CardiacSession session = sessionRepository.findById(activeSessionId).orElse(null);
        if (session != null) {
            HeartRateRecord record = new HeartRateRecord();
            record.setBpm(data.bpm());
            record.setTimestamp(LocalDateTime.now());
            record.setSession(session);
            heartRateRepository.save(record);

            //APPEL DU SERVICE DE CONTRÔLE DES SEUILS
            monitoringService.checkBpmThresholds(session.getPatientId(), data.bpm());

            // Diffusion WebSocket pour le graphique en temps réel
            messagingTemplate.convertAndSend("/topic/pulse", data);
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopActiveSession() {
        if (sessionManager.getActiveSessionId() == null) {
            return ResponseEntity.ok("Aucune session n'était active.");
        }

        // On récupère l'ID avant de le mettre à null pour le log ou la notification
        Long sessionId = sessionManager.getActiveSessionId();

        // ACTION CLÉ : On libère la session active
        sessionManager.stopSession();

        logger.info("Session {} arrêtée manuellement par l'utilisateur.", sessionId);

        return ResponseEntity.ok("Mesure arrêtée avec succès.");
    }

    @GetMapping("/session/{sessionId}/data")
    public List<HeartRateRecord> getSessionData(@PathVariable Long sessionId) {
        // On vérifie si la session existe d'abord
        if (!sessionRepository.existsById(sessionId)) {
            throw new RuntimeException("Session non trouvée");
        }

        // On retourne la liste des BPM avec leurs timestamps
        return heartRateRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    @GetMapping("/session/{sessionId}/summary")
    public Map<String, Object> getSessionSummary(@PathVariable Long sessionId) {
        List<HeartRateRecord> records = heartRateRepository.findBySessionIdOrderByTimestampAsc(sessionId);

        if (records.isEmpty()) return Map.of("message", "Aucune donnée");

        double average = records.stream().mapToInt(HeartRateRecord::getBpm).average().orElse(0.0);
        int max = records.stream().mapToInt(HeartRateRecord::getBpm).max().orElse(0);
        int min = records.stream().mapToInt(HeartRateRecord::getBpm).min().orElse(0);

        return Map.of(
                "averageBpm", average,
                "maxBpm", max,
                "minBpm", min,
                "totalPoints", records.size()
        );
    }

    @GetMapping("/patient/{patientId}/sessions")
    public ResponseEntity<List<CardiacSession>> getPatientSessions(@PathVariable String patientId) {
        List<CardiacSession> sessions = sessionRepository.findByPatientIdOrderByStartTimeDesc(patientId);

        if (sessions.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(sessions);
    }


//    @GetMapping("/history/{patientId}")
//    public List<CardiacSession> getHistory(@PathVariable String patientId) {
//        return sessionRepository.findByPatientIdOrderByStartTimeDesc(patientId);
//    }
}