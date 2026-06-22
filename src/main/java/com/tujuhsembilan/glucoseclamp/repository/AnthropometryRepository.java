package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Anthropometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnthropometryRepository extends JpaRepository<Anthropometry, Integer> {
    @Query("SELECT a FROM Anthropometry a WHERE a.deletedAt IS NULL")
    List<Anthropometry> findAllActive();

    @Query("SELECT a FROM Anthropometry a WHERE a.deletedAt IS NULL")
    org.springframework.data.domain.Page<Anthropometry> findAllActive(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT a FROM Anthropometry a WHERE a.anthroId = ?1 AND a.deletedAt IS NULL")
    Optional<Anthropometry> findByIdAndDeletedAtIsNull(Integer anthroId);

    @Query("SELECT a FROM Anthropometry a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    Optional<Anthropometry> findBySessionIdAndDeletedAtIsNull(Long sessionId);

    @Query("SELECT a FROM Anthropometry a WHERE a.deletedAt IS NULL AND (LOWER(CONCAT('', a.weightKg)) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(CONCAT('', a.heightCm)) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(CONCAT('', a.bmi)) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(CONCAT('', a.waistCircumferenceCm)) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(CONCAT('', a.session.sessionId)) LIKE LOWER(CONCAT('%', ?1, '%'))) ")
    org.springframework.data.domain.Page<Anthropometry> searchByKeyword(String keyword, org.springframework.data.domain.Pageable pageable);
}
