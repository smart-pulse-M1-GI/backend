package com.smartpulse.demo.controller;

import com.smartpulse.demo.model.DTO.ActivityRequest;
import com.smartpulse.demo.model.DTO.StartActivitySessionRequest;
import com.smartpulse.demo.model.entity.Activity;
import com.smartpulse.demo.model.entity.CardiacSession;
import com.smartpulse.demo.repository.*;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import com.smartpulse.demo.service.NotificationService;
import com.smartpulse.demo.service.SessionManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final SessionRepository sessionRepository;
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final SessionManagerService sessionManager;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    public ActivityController(ActivityRepository ar, NotificationService ns,
                              SessionRepository sr, SessionManagerService sm,
                              PatientRepository pr, MedecinRepository mr) {
        this.activityRepository = ar;
        this.notificationService = ns;
        this.sessionRepository = sr;
        this.sessionManager = sm;
        this.patientRepository = pr;
        this.medecinRepository = mr;

    }

    @PostMapping("/create")
    public Activity createActivity(@RequestBody ActivityRequest req) {
        Activity activity = new Activity();
        activity.setTitle(req.title());
        activity.setDescription(req.description());
        activity.setPatientId(req.patientId());
        activity.setDoctorId(req.doctorId());
        activity.setDurationInMinutes(req.durationInMinutes());

        Activity saved = activityRepository.save(activity);

        // Notifier le patient
        patientRepository.findById(Long.valueOf(req.patientId())).ifPresent(patient -> {
            notificationService.notifyUser(
                    String.valueOf(patient.getUser().getId()), // On utilise le mail de l'User
                    "Nouvelle activité prescrite par votre médecin : " + req.title()
            );
        });
        return saved;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/start-activity")
    public Long startActivitySession(@RequestBody StartActivitySessionRequest req) {
        Activity activity = activityRepository.findById(req.activityId())
                .orElseThrow(() -> new RuntimeException("Activité non trouvée"));

        // RÉGLE 1 : Si le médecin a fermé l'activité, on refuse
        if (activity.isCompleted()) {
            throw new RuntimeException("Cette activité est clôturée. Vous ne pouvez plus ajouter de mesures.");
        }

        CardiacSession session = new CardiacSession();
        session.setPatientId(req.patientId());
        session.setStartTime(LocalDateTime.now());
        session.setActivity(activity); // Liaison à l'activité

        CardiacSession saved = sessionRepository.save(session);
        sessionManager.setActiveSessionId(saved.getId());

        // Optionnel : Notifier le médecin que le patient commence
//        notificationService.notifyUser(activity.getDoctorId(),
//                "Le patient " + req.patientId() + " a commencé l'activité : " + activity.getTitle());

        // On programme une tâche qui s'exécutera après X minutes
        scheduler.schedule(() -> {
            // 1. On marque l'ID de session active à null pour arrêter de recevoir des données
            sessionManager.stopSession(); // Arrêt via le service

            // 2. On marque l'activité comme terminée
//            activity.setCompleted(true);
//            activityRepository.save(activity);

            // 3. On notifie le médecin
            medecinRepository.findById(Long.valueOf(activity.getDoctorId())).ifPresent(doc -> {
                notificationService.notifyUser(
                        String.valueOf(doc.getUser().getId()), // Mail de l'utilisateur médecin
                        "Le patient " + req.patientId() + " a terminé l'activité : " + activity.getTitle()
                );
            });

            System.out.println("Activité terminée, notification envoyée au médecin.");

        }, activity.getDurationInMinutes(), TimeUnit.MINUTES);

        return saved.getId();
    }

    // MODIFIER une activité
    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody ActivityRequest req) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setTitle(req.title());
                    activity.setDescription(req.description());
                    // On peut aussi imaginer modifier le statut 'completed' si besoin

                    Activity updated = activityRepository.save(activity);

                    // Notifier le patient du changement
                    patientRepository.findById(Long.valueOf(activity.getPatientId())).ifPresent(p ->
                            notificationService.notifyUser(String.valueOf(p.getUser().getId()),
                                    "L'activité '" + activity.getTitle() + "' a été mise à jour.")
                    );
                    return updated;
                })
                .orElseThrow(() -> new RuntimeException("Activité non trouvée avec l'id : " + id));
    }

    // SUPPRIMER une activité
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> {
                    String patientId = activity.getPatientId();
                    String title = activity.getTitle();

                    activityRepository.delete(activity);

                    // Notifier le patient de l'annulation
                    patientRepository.findById(Long.valueOf(patientId)).ifPresent(p ->
                            notificationService.notifyUser(String.valueOf(p.getUser().getId()),
                                    "L'activité '" + title + "' a été annulée par votre médecin.")
                    );
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeActivity(@PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setCompleted(true); // Seul le médecin peut faire passer à "terminé"
                    activityRepository.save(activity);

                    // On notifie le patient que l'activité est finie et qu'il n'a plus à mesurer
                    patientRepository.findById(id).ifPresent(p ->
                            notificationService.notifyUser(String.valueOf(p.getUser().getId()),
                                    "Votre médecin a clôturé l'activité : " + activity.getTitle())
                    );


                    return ResponseEntity.ok("Activité clôturée avec succès.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Utilisé par le patient pour voir ce qu'il doit faire
    @GetMapping("/patient/{patientId}")
    public List<Activity> getActivitiesForPatient(@PathVariable String patientId) {
        // On récupère les activités, les plus récentes en premier
        return activityRepository.findByPatientIdOrderByIdDesc(patientId);
    }

    // Utilisé par le médecin pour suivre toutes les prescriptions qu'il a faites
    @GetMapping("/doctor/{doctorId}")
    public List<Activity> getActivitiesByDoctor(@PathVariable String doctorId) {
        return activityRepository.findByDoctorId(doctorId);
    }
}