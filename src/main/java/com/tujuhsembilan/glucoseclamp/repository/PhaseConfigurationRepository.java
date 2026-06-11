package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.PhaseConfiguration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseConfigurationRepository extends JpaRepository<PhaseConfiguration, Long>, JpaSpecificationExecutor<PhaseConfiguration> {

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.deletedAt IS NULL ORDER BY pc.phaseConfPriority ASC, pc.phaseConfId ASC")
    List<PhaseConfiguration> findAllActive();

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.deletedAt IS NULL ORDER BY pc.phaseConfPriority ASC, pc.phaseConfId ASC")
    Page<PhaseConfiguration> findAllActive(Pageable pageable);

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfId = ?1 AND pc.deletedAt IS NULL")
    Optional<PhaseConfiguration> findByIdAndDeletedAtIsNull(Long phaseConfId);

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfCode = ?1 AND pc.deletedAt IS NULL")
    Optional<PhaseConfiguration> findByPhaseConfCodeAndDeletedAtIsNull(String phaseConfCode);

    @Query("SELECT MAX(pc.phaseConfPriority) FROM PhaseConfiguration pc WHERE pc.deletedAt IS NULL")
    Integer findMaxPriority();

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfPriority >= ?1 AND pc.phaseConfPriority <= ?2 AND pc.deletedAt IS NULL AND pc.phaseConfId != ?3 ORDER BY pc.phaseConfPriority ASC")
    List<PhaseConfiguration> findByPriorityBetweenAndNotId(Integer minPriority, Integer maxPriority, Long excludeId);

    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.deletedAt IS NULL AND (LOWER(pc.phaseConfCode) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(pc.phaseConfName) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(pc.phaseConfType) LIKE LOWER(CONCAT('%', ?1, '%'))) ORDER BY pc.phaseConfPriority ASC, pc.phaseConfId ASC")
    Page<PhaseConfiguration> searchByKeyword(String keyword, Pageable pageable);
}
