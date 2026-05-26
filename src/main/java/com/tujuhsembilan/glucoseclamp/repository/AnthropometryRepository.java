package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Anthropometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnthropometryRepository extends JpaRepository<Anthropometry, Integer> {
    @Query("SELECT a FROM Anthropometry a WHERE a.deletedAt IS NULL")
    List<Anthropometry> findAllActive();
    @Query("SELECT a FROM Anthropometry a WHERE a.anthroId = ?1 AND a.deletedAt IS NULL")
    Optional<Anthropometry> findByIdAndDeletedAtIsNull(Integer anthroId);
    @Query("SELECT a FROM Anthropometry a WHERE a.patient.patientId = ?1 AND a.deletedAt IS NULL")
    List<Anthropometry> findByPatientIdAndDeletedAtIsNull(String patientId);
    @Query("SELECT a FROM Anthropometry a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    Optional<Anthropometry> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
}
