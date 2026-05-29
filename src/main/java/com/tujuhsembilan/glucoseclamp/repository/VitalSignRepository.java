package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Integer> {
    @Query("SELECT v FROM VitalSign v WHERE v.deletedAt IS NULL")
    List<VitalSign> findAllActive();

    @Query("SELECT v FROM VitalSign v WHERE v.deletedAt IS NULL")
    Page<VitalSign> findAllActive(Pageable pageable);
    @Query("SELECT v FROM VitalSign v WHERE v.vitalId = ?1 AND v.deletedAt IS NULL")
    Optional<VitalSign> findByIdAndDeletedAtIsNull(Integer vitalId);
    @Query("SELECT v FROM VitalSign v WHERE v.session.sessionId = ?1 AND v.deletedAt IS NULL")
    List<VitalSign> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
}
