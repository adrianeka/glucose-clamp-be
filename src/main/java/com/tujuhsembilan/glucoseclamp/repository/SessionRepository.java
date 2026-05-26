package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("SELECT s FROM Session s WHERE s.deletedAt IS NULL")
    List<Session> findAllActive();
    @Query("SELECT s FROM Session s WHERE s.sessionId = ?1 AND s.deletedAt IS NULL")
    Optional<Session> findByIdAndDeletedAtIsNull(Integer sessionId);
    @Query("SELECT s FROM Session s WHERE s.patient.patientId = ?1 AND s.deletedAt IS NULL")
    List<Session> findByPatientIdAndDeletedAtIsNull(String patientId);
    @Query("SELECT s FROM Session s WHERE s.protocol.protocolId = ?1 AND s.deletedAt IS NULL")
    List<Session> findByProtocolIdAndDeletedAtIsNull(String protocolId);
    @Query("SELECT s FROM Session s WHERE s.visitDate = ?1 AND s.deletedAt IS NULL")
    List<Session> findByVisitDateAndDeletedAtIsNull(LocalDate visitDate);
}
