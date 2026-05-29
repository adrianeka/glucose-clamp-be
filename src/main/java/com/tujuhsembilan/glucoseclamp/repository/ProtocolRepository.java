package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Protocol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, String> {

    @Query("SELECT p FROM Protocol p WHERE p.deletedAt IS NULL")
    List<Protocol> findAllActive();

    @Query("SELECT p FROM Protocol p WHERE p.deletedAt IS NULL")
    Page<Protocol> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Protocol p WHERE p.protocolId = ?1 AND p.deletedAt IS NULL")
    Optional<Protocol> findByIdAndDeletedAtIsNull(String protocolId);

    @Query("SELECT p FROM Protocol p WHERE p.protocolCode = ?1 AND p.deletedAt IS NULL")
    Optional<Protocol> findByProtocolCodeAndDeletedAtIsNull(String protocolCode);

    Optional<Protocol> findByProtocolCode(String protocolCode);

    @Query("SELECT p FROM Protocol p WHERE p.deletedAt IS NULL " +
           "AND (:search IS NULL OR :search = '' " +
           "  OR LOWER(p.protocolId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.protocolCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.protocolName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.insulinDoseRule) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.insulinDoseUnit) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(p.glucoseTargetUnit) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR CAST(p.version AS string) LIKE CONCAT('%', :search, '%') " +
           "  OR CAST(p.durationHours AS string) LIKE CONCAT('%', :search, '%') " +
           "  OR CAST(p.glucoseTargetMin AS string) LIKE CONCAT('%', :search, '%') " +
           "  OR CAST(p.glucoseTargetMax AS string) LIKE CONCAT('%', :search, '%') " +
           ") " +
           "AND (CAST(:startDate AS timestamp) IS NULL OR p.createdAt >= :startDate) " +
           "AND (CAST(:endDate AS timestamp) IS NULL OR p.createdAt <= :endDate)")
    List<Protocol> searchProtocols(
        @Param("search") String search,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
