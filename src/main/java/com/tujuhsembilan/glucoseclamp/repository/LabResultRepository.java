package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import com.tujuhsembilan.glucoseclamp.model.LabResult;

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
public interface LabResultRepository extends JpaRepository<LabResult, Long> { // 1. Ubah String ke Long di sini
    
    @Query("SELECT lr FROM LabResult lr WHERE lr.deletedAt IS NULL")
    List<LabResult> findAllActive();

    @Query("SELECT lr FROM LabResult lr WHERE lr.deletedAt IS NULL")
    Page<LabResult> findAllActive(Pageable pageable);

    @Query("SELECT lr FROM LabResult lr WHERE lr.labResultId = ?1 AND lr.deletedAt IS NULL")
    Optional<LabResult> findByIdAndDeletedAtIsNull(Long labResultId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.bloodSample.bloodSampleId = ?1 AND lr.deletedAt IS NULL")
    Optional<LabResult> findByBloodSampleIdAndDeletedAtIsNull(Long bloodSampleId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.parameterName = ?1 AND lr.deletedAt IS NULL")
    List<LabResult> findByParameterNameAndDeletedAtIsNull(String parameterName);

    @Query("SELECT lr FROM LabResult lr WHERE lr.abnormalFlag = ?1 AND lr.deletedAt IS NULL")
    List<LabResult> findByAbnormalFlagAndDeletedAtIsNull(String abnormalFlag);

    @Query("SELECT lr FROM LabResult lr " +
           "WHERE lr.deletedAt IS NULL " +
           "  AND LOWER(lr.parameterName) = 'glucose' " +
           "  AND lr.bloodSample.deletedAt IS NULL " +
           "  AND lr.bloodSample.activity.deletedAt IS NULL " +
           "  AND lr.bloodSample.activity.session.sessionId = :sessionId " +
           "  AND (lr.bloodSample.activity.phaseCode LIKE '%BASE%' " +
           "       OR lr.bloodSample.activity.phaseCode LIKE '%BASE%')")
    List<LabResult> findBaselineGlucoseBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.deletedAt IS NULL " +
           "AND (:search IS NULL OR :search = '' " +
           "  OR CAST(lr.labResultId AS string) LIKE CONCAT('%', :search, '%') " + // 3. Gunakan CAST di sini
           "  OR CAST(lr.bloodSample.bloodSampleId AS string) LIKE CONCAT('%', :search, '%') " + // 3. Gunakan CAST di sini
           "  OR LOWER(lr.parameterName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(lr.unit) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(lr.abnormalFlag) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR CAST(lr.value AS string) LIKE CONCAT('%', :search, '%') " +
           "  OR CAST(lr.referenceRangeMin AS string) LIKE CONCAT('%', :search, '%') " +
           "  OR CAST(lr.referenceRangeMax AS string) LIKE CONCAT('%', :search, '%') " +
           ") " +
           "AND (CAST(:startDate AS timestamp) IS NULL OR lr.createdAt >= :startDate) " +
           "AND (CAST(:endDate AS timestamp) IS NULL OR lr.createdAt <= :endDate)")
    List<LabResult> searchLabResults(
        @Param("search") String search,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    List<LabResult> findByBloodSampleAndDeletedAtIsNull(BloodSample bloodSample);
}