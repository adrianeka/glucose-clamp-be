package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodSampleRepository extends JpaRepository<BloodSample, String> {
    @Query("SELECT bs FROM BloodSample bs WHERE bs.deletedAt IS NULL")
    List<BloodSample> findAllActive();
    @Query("SELECT bs FROM BloodSample bs WHERE bs.bloodSampleId = ?1 AND bs.deletedAt IS NULL")
    Optional<BloodSample> findByIdAndDeletedAtIsNull(String bloodSampleId);
    @Query("SELECT bs FROM BloodSample bs WHERE bs.activity.activityId = ?1 AND bs.deletedAt IS NULL")
    List<BloodSample> findByActivityIdAndDeletedAtIsNull(String activityId);
    @Query("SELECT bs FROM BloodSample bs WHERE bs.sampleType = ?1 AND bs.deletedAt IS NULL")
    List<BloodSample> findBySampleTypeAndDeletedAtIsNull(String sampleType);
}
