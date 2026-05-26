package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfusionMonitoringRepository extends JpaRepository<InfusionMonitoring, Integer> {
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL")
    List<InfusionMonitoring> findAllActive();
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.infusionId = ?1 AND i.deletedAt IS NULL")
    Optional<InfusionMonitoring> findByIdAndDeletedAtIsNull(Integer infusionId);
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.session.sessionId = ?1 AND i.deletedAt IS NULL")
    List<InfusionMonitoring> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
}
