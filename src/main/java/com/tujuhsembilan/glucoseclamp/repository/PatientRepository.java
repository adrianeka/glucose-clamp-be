package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    @Query("SELECT p FROM Patient p WHERE p.deletedAt IS NULL")
    Page<Patient> findAllActive(Pageable pageable);
    @Query("SELECT p FROM Patient p WHERE p.patientId = ?1 AND p.deletedAt IS NULL")
    Optional<Patient> findByIdAndDeletedAtIsNull(String patientId);
    @Query("SELECT p FROM Patient p WHERE p.medicalRecordNo = ?1 AND p.deletedAt IS NULL")
    Optional<Patient> findByMedicalRecordNoAndDeletedAtIsNull(String medicalRecordNo);
    @Query("SELECT p FROM Patient p WHERE p.name LIKE %?1% AND p.deletedAt IS NULL")
    List<Patient> findByNameContainingAndDeletedAtIsNull(String name);
    @Query(value = "SELECT * FROM patients WHERE deleted_at IS NULL ORDER BY patient_id DESC LIMIT 1", nativeQuery = true)
    Optional<Patient> findTopByOrderByPatientIdDesc();
}
