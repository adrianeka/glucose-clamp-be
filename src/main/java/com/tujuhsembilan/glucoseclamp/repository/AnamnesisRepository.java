package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Anamnesis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnamnesisRepository extends JpaRepository<Anamnesis, Integer> {
    @Query("SELECT a FROM Anamnesis a WHERE a.deletedAt IS NULL")
    List<Anamnesis> findAllActive();
    @Query("SELECT a FROM Anamnesis a WHERE a.anamnesisId = ?1 AND a.deletedAt IS NULL")
    Optional<Anamnesis> findByIdAndDeletedAtIsNull(Integer anamnesisId);
    @Query("SELECT a FROM Anamnesis a WHERE a.session.sessionId = ?1 AND a.deletedAt IS NULL")
    Optional<Anamnesis> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
}
