package com.smartpulse.demo.controller;

import com.smartpulse.demo.service.UserService;
import com.smartpulse.demo.model.DTO.UserProfileResponse;
import com.smartpulse.demo.model.DTO.UpdateProfileRequest;
import com.smartpulse.demo.model.Enum.Role;
import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.model.entity.Patient;
import com.smartpulse.demo.model.entity.User;
import com.smartpulse.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // user est chargé grâce au filtre JWT
        if (userRepository.findByMail(userDetails.getUsername()).isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        User user = userRepository.findByMail(userDetails.getUsername()).get();
        UserProfileResponse response = userService.getUserInfo(user);
        return ResponseEntity.ok(response);
    }

    // 1. Obtenir les infos sur le MÉDECIN du patient connecté
    @GetMapping("/my-doctor")
    public ResponseEntity<UserProfileResponse> getMyDoctor(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByMail(userDetails.getUsername()).get();
        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil patient non trouvé"));

        if (patient.getMedecin() == null) return ResponseEntity.noContent().build();

        // On récupère le User lié au Médecin pour générer la réponse
        return ResponseEntity.ok(userService.getUserInfo(patient.getMedecin().getUser()));
    }

    // 2. Obtenir la liste des PATIENTS du médecin connecté
    @GetMapping("/my-patients")
    public ResponseEntity<List<UserProfileResponse>> getMyPatients(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByMail(userDetails.getUsername()).get();
        Medecin medecin = medecinRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil médecin non trouvé"));

        List<Patient> patients = patientRepository.findByMedecin(medecin);
        List<UserProfileResponse> response = patients.stream()
                .map(p -> userService.getUserInfo(p.getUser()))
                .toList();

        return ResponseEntity.ok(response);
    }

    // 3. Modifier son profil (Patient ou Médecin) + mot de passe
    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest req) {

        User user = userRepository.findByMail(userDetails.getUsername()).get();

        if (user.getRole() == Role.PATIENT) {
            Patient p = patientRepository.findByUser(user).get();
            p.setNom(req.nom());
            p.setPrenom(req.prenom());
            p.setDateNaissance(req.dateNaissance());
            patientRepository.save(p);
        } else {
            Medecin m = medecinRepository.findByUser(user).get();
            m.setNom(req.nom());
            m.setPrenom(req.prenom());
            m.setSpecialite(req.specialite());
            m.setDateNaissance(req.dateNaissance());
            medecinRepository.save(m);
        }

        // Mise à jour du mot de passe si fourni
        if (req.password() != null && !req.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.password()));
            userRepository.save(user);
        }

        return ResponseEntity.ok(userService.getUserInfo(user));
    }

}
