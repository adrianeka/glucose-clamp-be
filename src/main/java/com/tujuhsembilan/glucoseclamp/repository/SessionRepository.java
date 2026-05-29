package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.dto.response.SessionSummaryResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("SELECT s FROM Session s WHERE s.deletedAt IS NULL")
    Page<Session> findAllActive(Pageable pageable);

    @Query("""
            SELECT new com.tujuhsembilan.glucoseclamp.dto.response.SessionSummaryResponse(
                s.sessionId,
                p.patientId,
                p.name,
                pr.protocolId,
                pr.protocolName,
                s.visitDate,
                s.startTime,
                s.endTime,
                s.sessionStatus,
                COALESCE((SELECT COUNT(a) FROM Activity a WHERE a.session = s AND a.deletedAt IS NULL), 0),
                COALESCE((SELECT COUNT(a2) FROM Activity a2 WHERE a2.session = s AND a2.deletedAt IS NULL AND a2.activityStatus = com.tujuhsembilan.glucoseclamp.model.base.ActivityStatus.DONE), 0)
            )
            FROM Session s
            JOIN s.patient p
            JOIN s.protocol pr
            WHERE s.deletedAt IS NULL
            """)
    Page<SessionSummaryResponse> findAllSessionSummaries(Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.sessionId = ?1 AND s.deletedAt IS NULL")
    Optional<Session> findByIdAndDeletedAtIsNull(Integer sessionId);
    @Query("SELECT s FROM Session s WHERE s.patient.patientId = ?1 AND s.deletedAt IS NULL")
    List<Session> findByPatientIdAndDeletedAtIsNull(String patientId);
    @Query("SELECT s FROM Session s WHERE s.protocol.protocolId = ?1 AND s.deletedAt IS NULL")
    List<Session> findByProtocolIdAndDeletedAtIsNull(String protocolId);
    @Query("SELECT s FROM Session s WHERE s.visitDate = ?1 AND s.deletedAt IS NULL")
    List<Session> findByVisitDateAndDeletedAtIsNull(LocalDate visitDate);
}
