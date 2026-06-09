package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.GlobalConfiguration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalConfigurationRepository extends JpaRepository<GlobalConfiguration, Integer> {
    @Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.deletedAt IS NULL")
    List<GlobalConfiguration> findAllActive();
    @org.springframework.data.jpa.repository.Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.deletedAt IS NULL")
    Page<GlobalConfiguration> findAllActive(Pageable pageable);
    @Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.gconfId = ?1 AND gc.deletedAt IS NULL")
    Optional<GlobalConfiguration> findByIdAndDeletedAtIsNull(Integer gconfId);
    @Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.gconfCode = ?1 AND gc.deletedAt IS NULL")
    Optional<GlobalConfiguration> findByCodeAndDeletedAtIsNull(String gconfCode);

    @Query("SELECT gc FROM GlobalConfiguration gc WHERE gc.deletedAt IS NULL AND (LOWER(gc.gconfCode) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(gc.gconfValue) LIKE LOWER(CONCAT('%', ?1, '%'))) ")
    Page<GlobalConfiguration> searchByKeyword(String keyword, Pageable pageable);
}
