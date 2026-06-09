package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Participant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, String> {
    @Query("SELECT p FROM Participant p WHERE p.deletedAt IS NULL")
    Page<Participant> findAllActive(Pageable pageable);
    @Query("SELECT p FROM Participant p WHERE p.participantId = ?1 AND p.deletedAt IS NULL")
    Optional<Participant> findByIdAndDeletedAtIsNull(String participantId);
    @Query("SELECT p FROM Participant p WHERE p.medicalRecordNo = ?1 AND p.deletedAt IS NULL")
    Optional<Participant> findByMedicalRecordNoAndDeletedAtIsNull(String medicalRecordNo);
    @Query("SELECT p FROM Participant p WHERE p.name LIKE %?1% AND p.deletedAt IS NULL")
    List<Participant> findByNameContainingAndDeletedAtIsNull(String name);
    @Query("SELECT p FROM Participant p WHERE p.deletedAt IS NULL AND (LOWER(p.participantId) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(p.medicalRecordNo) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(COALESCE(p.numberPhone, '')) LIKE LOWER(CONCAT('%', ?1, '%')))")
    Page<Participant> searchByKeyword(String keyword, Pageable pageable);
    @Query(value = "SELECT * FROM participants WHERE deleted_at IS NULL ORDER BY participant_id DESC LIMIT 1", nativeQuery = true)
    Optional<Participant> findTopByOrderByParticipantIdDesc();
}
