package com.smartpulse.demo.controller;

import com.smartpulse.demo.service.AuthService;
import com.smartpulse.demo.service.JwtService;
import com.smartpulse.demo.model.DTO.AuthResponse;
import com.smartpulse.demo.model.DTO.LoginRequest;
import com.smartpulse.demo.model.DTO.RegisterMedecinRequest;
import com.smartpulse.demo.model.DTO.RegisterPatientRequest;
import com.smartpulse.demo.model.Enum.Role;
import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.repository.MedecinRepository;
import com.smartpulse.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.smartpulse.demo.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor

@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.mail(), request.password())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token)); // ✅ Format cohérent
    }

    @PostMapping("/register/medecin")
    public ResponseEntity<AuthResponse> registerMedecin(@RequestBody RegisterMedecinRequest request) {
        AuthResponse response = authService.registerMedecin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@RequestBody RegisterPatientRequest request) {
        AuthResponse response = authService.registerPatient(request);
        return ResponseEntity.ok(response);
    }

    // Pour tester rapidement → on crée un médecin à la main
    @PostMapping("/register-test")
    public String registerTest() {
        User user = new User();
        user.setMail("doc@exemple.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(Role.MEDECIN);

        Medecin medecin = new Medecin();
        medecin.setSpecialite("Neurologue");
        medecin.setUser(user);
        medecin.setNom("John");
        medecin.setPrenom("Doe");

        userRepository.save(user);
        medecinRepository.save(medecin);
        return "Médecin créé → login : doc@exemple.com / 123456";
    }
}
