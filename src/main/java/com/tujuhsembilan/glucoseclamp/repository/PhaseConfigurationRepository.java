package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.PhaseConfiguration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhaseConfigurationRepository extends JpaRepository<PhaseConfiguration, Integer> {
    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.deletedAt IS NULL")
    List<PhaseConfiguration> findAllActive();
    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfigId = ?1 AND pc.deletedAt IS NULL")
    Optional<PhaseConfiguration> findByIdAndDeletedAtIsNull(Integer phaseConfigId);
    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfigCode = ?1 AND pc.deletedAt IS NULL")
    Optional<PhaseConfiguration> findByCodeAndDeletedAtIsNull(String phaseConfigCode);
    @Query("SELECT pc FROM PhaseConfiguration pc WHERE pc.phaseConfigType = ?1 AND pc.deletedAt IS NULL")
    Optional<PhaseConfiguration> findByTypeAndDeletedAtIsNull(String phaseConfigType);
}
