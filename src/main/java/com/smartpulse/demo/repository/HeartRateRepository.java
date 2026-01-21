package com.smartpulse.demo.repository;

import com.smartpulse.demo.model.entity.HeartRateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartRateRepository extends JpaRepository<HeartRateRecord, Long> {
    //pour supprimer les données d'une session si besoin
    void deleteBySessionId(Long sessionId);
    List<HeartRateRecord> findBySessionIdOrderByTimestampAsc(Long sessionId);
}
