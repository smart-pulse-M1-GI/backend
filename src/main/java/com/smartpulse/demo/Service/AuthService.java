package com.smartpulse.demo.Service;

import com.smartpulse.demo.model.DTO.AuthResponse;
import com.smartpulse.demo.model.DTO.LoginRequest;
import com.smartpulse.demo.model.DTO.RegisterMedecinRequest;
import com.smartpulse.demo.model.DTO.RegisterPatientRequest;
import com.smartpulse.demo.model.Enum.Role;
import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.model.entity.Patient;
import com.smartpulse.demo.repository.MedecinRepository;
import com.smartpulse.demo.repository.PatientRepository;
import com.smartpulse.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.smartpulse.demo.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// src/main/java/com/smartpulse/demo/service/AuthService.java
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //Methode permettant de creer un medecin
    public AuthResponse registerMedecin(RegisterMedecinRequest request) {
        // Vérifier si mail déjà utilisé
        if (userRepository.findByMail(request.mail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Créer l'utilisateur
        User user = createUser(request.mail(), request.password(), Role.MEDECIN);


        // Créer le profil médecin
        Medecin medecin = new Medecin();
        medecin.setSpecialite(request.specialite());
        medecin.setUser(user);
        medecin.setNom(request.nom());
        medecin.setPrenom(request.prenom());
        medecin.setDateNaissance(request.dateNaissance());
        medecin.setUser(user);

        userRepository.save(user);
        medecinRepository.save(medecin);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    //Methode pour creer un patient
    public AuthResponse registerPatient(RegisterPatientRequest request) {
        if (userRepository.findByMail(request.mail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Créer l'utilisateur
        User user = createUser(request.mail(), request.password(), Role.PATIENT);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setNom(request.nom());
        patient.setPrenom(request.prenom());
        patient.setDateNaissance(request.dateNaissance());

        // Assignation au médecin si fourni
        if (request.medecinId() != null) {
            Medecin medecin = medecinRepository.findById(request.medecinId())
                    .orElseThrow(() -> new RuntimeException("Médecin "+request.medecinId()+" non trouvé"));
            patient.setMedecin(medecin);
        }

        userRepository.save(user);
        patientRepository.save(patient);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    //Methode utilitaire pour creer un user
    private User createUser(String mail, String password, Role role){
        User user = new User();

        user.setMail(mail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return user;
    }
}