package com.smartpulse.demo.repository;

import java.util.Optional;

import com.smartpulse.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpulse.demo.model.entity.Medecin;
import org.springframework.stereotype.Repository;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByUser_Id(Long userId);
    Optional<Medecin> findByUser(User user);
}
