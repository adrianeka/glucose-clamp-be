package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodSampleRepository extends JpaRepository<BloodSample, String> {

    @Query("SELECT b FROM BloodSample b WHERE b.deletedAt IS NULL")
    List<BloodSample> findAllActive();

    @Query("SELECT b FROM BloodSample b WHERE b.deletedAt IS NULL")
    Page<BloodSample> findAllActive(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT b FROM BloodSample b WHERE b.bloodSampleId = ?1 AND b.deletedAt IS NULL")
    Optional<BloodSample> findByBloodSampleIdAndDeletedAtIsNull(String bloodSampleId);

    @Query("SELECT b FROM BloodSample b WHERE b.deletedAt IS NULL AND (LOWER(b.sampleCode) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(b.sampleType) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(b.tubeType) LIKE LOWER(CONCAT('%', ?1, '%'))) ")
    Page<BloodSample> searchByKeyword(String keyword, Pageable pageable);

    @Query("SELECT b FROM BloodSample b WHERE b.deletedAt IS NULL ORDER BY b.bloodSampleId DESC")
    Optional<BloodSample> findTopByDeletedAtIsNullOrderByBloodSampleIdDesc();
}
