package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InfusionMonitoringRepository extends JpaRepository<InfusionMonitoring, Long> { 

    List<InfusionMonitoring> findBySessionAndStatusAndDeletedAtIsNull(Session session, EntityStatus status);

    @Query("SELECT i FROM InfusionMonitoring i WHERE i.deletedAt IS NULL")
    List<InfusionMonitoring> findAllActive();

    @Query("SELECT im FROM InfusionMonitoring im " +
           "WHERE im.status = 'ACTIVE' " +
           "  AND im.deletedAt IS NULL " +
           "  AND (:includeSystem = true OR im.createdBy != 0)")
    Page<InfusionMonitoring> findAllActive(@Param("includeSystem") boolean includeSystem, Pageable pageable);

    @Query("SELECT i FROM InfusionMonitoring i WHERE i.infusionId = ?1 AND i.deletedAt IS NULL")
    Optional<InfusionMonitoring> findByIdAndDeletedAtIsNull(Long infusionId); 

    @Query("""
           SELECT im FROM InfusionMonitoring im 
           JOIN FETCH im.session s 
           JOIN FETCH s.protocol p 
           WHERE im.infusionId = :id AND im.deletedAt IS NULL
           """)
    Optional<InfusionMonitoring> findByIdWithSessionAndProtocol(@Param("id") Long id); 
    
    @Query("SELECT i FROM InfusionMonitoring i WHERE i.session.sessionId = ?1 AND i.deletedAt IS NULL")
    List<InfusionMonitoring> findBySessionIdAndDeletedAtIsNull(Integer sessionId);

    @Query("SELECT im FROM InfusionMonitoring im " +
           "WHERE im.deletedAt IS NULL " +
           "  AND (:includeSystem = true OR im.createdBy != 0) " +
           "  AND (CAST(im.infusionId AS string) LIKE CONCAT('%', :keyword, '%') OR " + // Mengubah LOWER() menjadi CAST()
           "       LOWER(im.adjustmentNote) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<InfusionMonitoring> searchByKeyword(
        @Param("keyword") String keyword, 
        @Param("includeSystem") boolean includeSystem, 
        Pageable pageable
    );

    Optional<InfusionMonitoring> findTopByDeletedAtIsNullOrderByInfusionIdDesc();

    Optional<InfusionMonitoring> findTopBySessionAndStatusAndDeletedAtIsNullOrderByTimeDesc(Session session, EntityStatus status);

    boolean existsBySessionAndTimeAndStatusAndDeletedAtIsNull(
        Session session, 
        LocalDateTime time, 
        EntityStatus status
    );
    @Query("SELECT im FROM InfusionMonitoring im " +
       "WHERE im.session.sessionId = :sessionId " +
       "AND im.deletedAt IS NULL" +
        " AND im.createdBy != 0")
    Page<InfusionMonitoring> findBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    
    List<InfusionMonitoring>
    findBySessionSessionIdAndDeletedAtIsNullOrderByTimeAsc(
            Long sessionId
    );
}