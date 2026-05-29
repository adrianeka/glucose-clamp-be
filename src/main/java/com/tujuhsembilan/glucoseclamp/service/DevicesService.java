package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.DeviceRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.DeviceStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.DeviceUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.DeviceResponse;
import com.tujuhsembilan.glucoseclamp.model.Device;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.DeviceRepository;
import com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DevicesService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllDevices(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<DeviceResponse> result = deviceRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data perangkat")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getDeviceById(Integer id) {
        Device device = deviceRepository.findByIdAndDeletedAtIsNull(id).orElse(null);

        if (device == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data perangkat tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(device))
                .message("Berhasil mendapatkan data perangkat")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addDevice(DeviceRequest request) {
        if (deviceRepository.findBySerialNumberAndDeletedAtIsNull(request.getSerialNumber()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Serial number sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        Device device = Device.builder()
                .deviceType(request.getDeviceType())
                .deviceBrand(request.getDeviceBrand())
                .serialNumber(request.getSerialNumber())
                .build();

        try {
            if (request.getLastCalibrationDate() != null) {
                device.setLastCalibrationDate(LocalDateTime.parse(request.getLastCalibrationDate()));
            }
        } catch (DateTimeParseException ignored) {
        }

        LocalDateTime now = LocalDateTime.now();
        device.setCreatedAt(now);
        device.setUpdatedAt(now);
        device.setCreatedBy(currentUserId);
        device.setUpdatedBy(currentUserId);
        device.setStatus(EntityStatus.ACTIVE);

        deviceRepository.save(device);
        log.info("Device berhasil ditambahkan: {} oleh user {}", device.getDeviceId(), currentUserId);

        return ApiDataResponseBuilder.builder()
        .data(mapToResponse(device))
                .message("Perangkat berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateDevice(Integer id, DeviceUpdateRequest request) {
        Optional<Device> opt = deviceRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data perangkat tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Device device = opt.get();
        Integer currentUserId = getCurrentUserId();

        if (request.getDeviceType() != null) device.setDeviceType(request.getDeviceType());
        if (request.getDeviceBrand() != null) device.setDeviceBrand(request.getDeviceBrand());
        if (request.getSerialNumber() != null) device.setSerialNumber(request.getSerialNumber());
        try {
            if (request.getLastCalibrationDate() != null) device.setLastCalibrationDate(LocalDateTime.parse(request.getLastCalibrationDate()));
        } catch (DateTimeParseException ignored) {
        }

        device.setUpdatedBy(currentUserId);

        deviceRepository.save(device);

        return ApiDataResponseBuilder.builder()
        .data(mapToResponse(device))
                .message("Perangkat berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteDevice(Integer id) {
        Optional<Device> opt = deviceRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data perangkat tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Device device = opt.get();
        Integer currentUserId = getCurrentUserId();

        device.setDeletedAt(LocalDateTime.now());
        device.setDeletedBy(currentUserId);
        device.setStatus(EntityStatus.DELETED);

        deviceRepository.save(device);
        log.info("Device {} berhasil dihapus (soft) oleh user {}", id, currentUserId);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(device))
                .message("Perangkat berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateDeviceStatus(Integer id, DeviceStatusUpdateRequest request) {
        Optional<Device> opt = deviceRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data perangkat tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status perangkat tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Device device = opt.get();
        Integer currentUserId = getCurrentUserId();

        device.setStatus(request.getStatus());
        device.setUpdatedBy(currentUserId);
        device.setUpdatedAt(LocalDateTime.now());

        deviceRepository.save(device);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(device))
                .message("Status perangkat berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchDevices(String keyword) {
        List<DeviceResponse> result = deviceRepository.searchByKeyword(keyword == null ? "" : keyword).stream()
                .map(this::mapToResponse)
                .toList();

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari perangkat")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private DeviceResponse mapToResponse(Device device) {
        DeviceResponse response = modelMapper.map(device, DeviceResponse.class);
        response.setStatus(device.getStatus() == null ? null : device.getStatus().name());
        return response;
    }
}
