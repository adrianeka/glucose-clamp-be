package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, String> {
    @Query("SELECT lr FROM LabResult lr WHERE lr.deletedAt IS NULL")
    List<LabResult> findAllActive();
    @Query("SELECT lr FROM LabResult lr WHERE lr.labResultId = ?1 AND lr.deletedAt IS NULL")
    Optional<LabResult> findByIdAndDeletedAtIsNull(String labResultId);
    @Query("SELECT lr FROM LabResult lr WHERE lr.bloodSample.bloodSampleId = ?1 AND lr.deletedAt IS NULL")
    Optional<LabResult> findByBloodSampleIdAndDeletedAtIsNull(String bloodSampleId);
    @Query("SELECT lr FROM LabResult lr WHERE lr.parameterName = ?1 AND lr.deletedAt IS NULL")
    List<LabResult> findByParameterNameAndDeletedAtIsNull(String parameterName);
    @Query("SELECT lr FROM LabResult lr WHERE lr.abnormalFlag = ?1 AND lr.deletedAt IS NULL")
    List<LabResult> findByAbnormalFlagAndDeletedAtIsNull(String abnormalFlag);
}
