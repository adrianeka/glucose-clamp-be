package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfusionMonitoringRepository extends JpaRepository<InfusionMonitoring, String> {
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL")
    List<InfusionMonitoring> findAllActive();
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL")
    Page<InfusionMonitoring> findAllActive(Pageable pageable);
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.infusionId = ?1 AND i.deletedAt IS NULL")
    Optional<InfusionMonitoring> findByIdAndDeletedAtIsNull(String infusionId);
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.session.sessionId = ?1 AND i.deletedAt IS NULL")
    List<InfusionMonitoring> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL AND (LOWER(i.adjustmentNote) LIKE LOWER(CONCAT('%', ?1, '%')) )")
    Page<InfusionMonitoring> searchByKeyword(String keyword, Pageable pageable);

    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL ORDER BY i.infusionId DESC")
    Optional<InfusionMonitoring> findTopByDeletedAtIsNullOrderByInfusionIdDesc();

    Optional<InfusionMonitoring> findTopBySessionAndStatusAndDeletedAtIsNullOrderByTimeDesc(Session session, EntityStatus status);
}
