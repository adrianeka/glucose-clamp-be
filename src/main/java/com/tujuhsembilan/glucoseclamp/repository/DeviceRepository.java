package com.tujuhsembilan.glucoseclamp.repository;

import com.tujuhsembilan.glucoseclamp.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    @Query("SELECT d FROM Device d WHERE d.deletedAt IS NULL")
    List<Device> findAllActive();
    @Query("SELECT d FROM Device d WHERE d.deviceId = ?1 AND d.deletedAt IS NULL")
    Optional<Device> findByIdAndDeletedAtIsNull(Integer deviceId);
    @Query("SELECT d FROM Device d WHERE d.serialNumber = ?1 AND d.deletedAt IS NULL")
    Optional<Device> findBySerialNumberAndDeletedAtIsNull(String serialNumber);
    @Query("SELECT d FROM Device d WHERE d.deviceType = ?1 AND d.deletedAt IS NULL")
    List<Device> findByDeviceTypeAndDeletedAtIsNull(String deviceType);
}
