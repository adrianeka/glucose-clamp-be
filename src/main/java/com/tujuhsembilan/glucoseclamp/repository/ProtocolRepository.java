package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Protocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProtocolRepository extends JpaRepository<Protocol, String> {
    @Query("SELECT p FROM Protocol p WHERE p.deletedAt IS NULL")
    List<Protocol> findAllActive();
    @Query("SELECT p FROM Protocol p WHERE p.protocolId = ?1 AND p.deletedAt IS NULL")
    Optional<Protocol> findByIdAndDeletedAtIsNull(String protocolId);
    @Query("SELECT p FROM Protocol p WHERE p.protocolCode = ?1 AND p.deletedAt IS NULL")
    Optional<Protocol> findByProtocolCodeAndDeletedAtIsNull(String protocolCode);
}
