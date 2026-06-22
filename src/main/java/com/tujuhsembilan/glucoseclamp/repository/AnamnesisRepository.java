package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Anamnesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AnamnesisRepository extends JpaRepository<Anamnesis, Integer> {
    @Query("SELECT a FROM Anamnesis a WHERE a.deletedAt IS NULL")
    Page<Anamnesis> findAllActive(Pageable pageable);
    @Query("SELECT a FROM Anamnesis a WHERE a.anamnesisId = ?1 AND a.deletedAt IS NULL")
    Optional<Anamnesis> findByIdAndDeletedAtIsNull(Integer anamnesisId);
    @Query("SELECT a FROM Anamnesis a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    Optional<Anamnesis> findBySessionIdAndDeletedAtIsNull(Long sessionId);

    @Query("SELECT a FROM Anamnesis a WHERE (LOWER(a.chiefComplaint) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(a.medicalHistory) LIKE LOWER(CONCAT('%', ?1, '%'))) AND a.deletedAt IS NULL")
    Page<Anamnesis> searchByKeyword(String keyword, Pageable pageable);
}
