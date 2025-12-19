package com.smartpulse.demo.Service;

import com.smartpulse.demo.model.DTO.UserProfileResponse;
import com.smartpulse.demo.model.Enum.Role;
import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.model.entity.Patient;
import com.smartpulse.demo.model.entity.User;
import com.smartpulse.demo.repository.MedecinRepository;
import com.smartpulse.demo.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;

    //Methode qui renvoi toutes les informations de l'utilisateur connecté
    public UserProfileResponse getUserInfo(User user){
        //On va chercher la specialite s'il s'agit d'un medecin
        //ou alors l'Id du medecin s'il s'agit d'un patient
        String specialite = null;
        Long medecinId = null;
        String nom = null;
        String prenom = null;
        LocalDate dateNaissance = null;
        Long id = null;


        if (user.getRole() == Role.MEDECIN) {
            specialite = medecinRepository.findByUser_Id(user.getId())
                    .map(Medecin::getSpecialite)
                    .orElse(null);
            nom = medecinRepository.findByUser_Id(user.getId())
                    .map(Medecin::getNom)
                    .orElse(null);
            prenom = medecinRepository.findByUser_Id(user.getId())
                    .map(Medecin::getPrenom)
                    .orElse(null);
            dateNaissance = medecinRepository.findByUser_Id(user.getId())
                    .map(Medecin::getDateNaissance)
                    .orElse(null);
            id = medecinRepository.findByUser_Id(user.getId())
                    .map(Medecin::getId)
                    .orElse(null);
        } else if (user.getRole() == Role.PATIENT) {
            medecinId = patientRepository.findByUser_Id(user.getId())
                    .map(Patient::getMedecin).map(Medecin::getId)
                    .orElse(null);
            nom = patientRepository.findByUser_Id(user.getId())
                    .map(Patient::getNom)
                    .orElse(null);
            prenom = patientRepository.findByUser_Id(user.getId())
                    .map(Patient::getPrenom)
                    .orElse(null);
            dateNaissance = patientRepository.findByUser_Id(user.getId())
                    .map(Patient::getDateNaissance)
                    .orElse(null);
            id = patientRepository.findByUser_Id(user.getId())
                    .map(Patient::getId)
                    .orElse(null);
        }

        return new UserProfileResponse(
                user.getId(),
                user.getMail(),
                nom,
                prenom,
                dateNaissance,
                user.getRole().name(),
                specialite,
                medecinId,
                id
        );
    }
}
