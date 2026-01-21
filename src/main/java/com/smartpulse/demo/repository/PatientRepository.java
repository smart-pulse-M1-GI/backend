package com.smartpulse.demo.repository;

import java.util.List;
import java.util.Optional;

import com.smartpulse.demo.model.entity.Medecin;
import com.smartpulse.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpulse.demo.model.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser_Id(Long userId);
    Optional<Patient> findByUser(User user);
    List<Patient> findByMedecin(Medecin medecin);
    @Query("SELECT p FROM Patient p WHERE p.user.mail = :mail")
    Optional<Patient> findByUserMail(String mail);
}
