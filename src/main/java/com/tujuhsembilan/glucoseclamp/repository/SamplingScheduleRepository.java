package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.SamplingSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SamplingScheduleRepository extends JpaRepository<SamplingSchedule, Long> {

    @Query("SELECT pd FROM SamplingSchedule pd WHERE pd.deletedAt IS NULL")
    List<SamplingSchedule> findAllActive();

    @Query("SELECT pd FROM SamplingSchedule pd WHERE pd.deletedAt IS NULL")
    Page<SamplingSchedule> findAllActive(Pageable pageable);

    @Query("SELECT pd FROM SamplingSchedule pd WHERE pd.samplingScheduleId = ?1 AND pd.deletedAt IS NULL")
    Optional<SamplingSchedule> findByIdAndDeletedAtIsNull(Long samplingScheduleId);

    @Query("SELECT pd FROM SamplingSchedule pd WHERE pd.protocol.protocolId = ?1 AND pd.deletedAt IS NULL")
    List<SamplingSchedule> findByProtocolIdAndDeletedAtIsNull(Long protocolId);

    @Query("SELECT pd FROM SamplingSchedule pd WHERE pd.deletedAt IS NULL " +
           "AND (:protocolId IS NULL OR :protocolId = '' OR pd.protocol.protocolId = :protocolId) " +
           "AND (:search IS NULL OR :search = '' " +
        //    "  OR LOWER(pd.samplingScheduleId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(pd.phaseCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
        //    "  OR LOWER(pd.protocol.protocolId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(pd.protocol.protocolName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR CAST(pd.timeInterval AS string) LIKE CONCAT('%', :search, '%') " +
           ") " +
           "AND (CAST(:startDate AS timestamp) IS NULL OR pd.createdAt >= :startDate) " +
           "AND (CAST(:endDate AS timestamp) IS NULL OR pd.createdAt <= :endDate)")
    List<SamplingSchedule> searchSamplingSchedules(
        @Param("protocolId") Long protocolId,
        @Param("search") String search,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    Optional<SamplingSchedule> findTopByProtocolProtocolIdAndStatusOrderByRelativeMinuteDesc(
            Long protocolId,
            EntityStatus status
    );

    List<SamplingSchedule>
    findByProtocolProtocolIdAndDeletedAtIsNullOrderByRelativeMinuteAsc(
            Long protocolId
    );

    Optional<SamplingSchedule>
    findTopByProtocolProtocolIdAndScheduleCodeStartingWithAndStatusOrderBySamplingScheduleIdDesc(
            Long protocolId,
            String prefix,
            EntityStatus status
    );
    
    List<SamplingSchedule> findByProtocolProtocolIdAndPhaseCodeAndDeletedAtIsNull(Long protocolId, String phaseCode);

    long countByProtocolProtocolIdAndPhaseCodeNotAndPhaseCodeNotAndStatus(Long protocolId, String pc1, String pc2, EntityStatus status);

    List<SamplingSchedule> findByProtocolProtocolIdAndStatusOrderByRelativeMinuteAsc(Long protocolId, EntityStatus status);
    List<SamplingSchedule> findByProtocolProtocolIdAndPhaseCodeAndStatus(Long protocolId,String phaseCode, EntityStatus status);

    @Modifying
    @Query("""
        UPDATE SamplingSchedule s
        SET
            s.status = :deletedStatus,
            s.deletedAt = :deletedAt,
            s.deletedBy = :deletedBy,
            s.scheduleCode = concat(s.scheduleCode, '_DEL_', cast(gen_random_uuid() as text))
        WHERE s.protocol.protocolId = :protocolId
        AND s.phaseCode = :phaseCode
        AND s.status = :activeStatus
    """)
    int bulkSoftDeleteByProtocolAndPhase(
            @Param("protocolId") Long protocolId,
            @Param("phaseCode") String phaseCode,
            @Param("deletedStatus") EntityStatus deletedStatus,
            @Param("activeStatus") EntityStatus activeStatus, // Tambahkan parameter ini
            @Param("deletedAt") LocalDateTime deletedAt,
            @Param("deletedBy") Integer deletedBy
    );
}
