package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.ProtocolDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProtocolDetailRepository extends JpaRepository<ProtocolDetail, String> {
    @Query("SELECT pd FROM ProtocolDetail pd WHERE pd.deletedAt IS NULL")
    List<ProtocolDetail> findAllActive();
    @Query("SELECT pd FROM ProtocolDetail pd WHERE pd.protocolDetailId = ?1 AND pd.deletedAt IS NULL")
    Optional<ProtocolDetail> findByIdAndDeletedAtIsNull(String protocolDetailId);
    @Query("SELECT pd FROM ProtocolDetail pd WHERE pd.protocol.protocolId = ?1 AND pd.deletedAt IS NULL")
    List<ProtocolDetail> findByProtocolIdAndDeletedAtIsNull(String protocolId);
}
