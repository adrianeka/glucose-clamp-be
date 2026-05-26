package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.SessionDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionDeviceRepository extends JpaRepository<SessionDevice, Integer> {
    @Query("SELECT sd FROM SessionDevice sd WHERE sd.deletedAt IS NULL")
    List<SessionDevice> findAllActive();
    @Query("SELECT sd FROM SessionDevice sd WHERE sd.sessionDeviceId = ?1 AND sd.deletedAt IS NULL")
    Optional<SessionDevice> findByIdAndDeletedAtIsNull(Integer sessionDeviceId);
    @Query("SELECT sd FROM SessionDevice sd WHERE sd.session.sessionId = ?1 AND sd.deletedAt IS NULL")
    List<SessionDevice> findBySessionIdAndDeletedAtIsNull(Integer sessionId);
    @Query("SELECT sd FROM SessionDevice sd WHERE sd.device.deviceId = ?1 AND sd.deletedAt IS NULL")
    List<SessionDevice> findByDeviceIdAndDeletedAtIsNull(Integer deviceId);
}
