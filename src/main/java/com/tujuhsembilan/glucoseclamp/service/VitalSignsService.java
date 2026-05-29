package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.VitalSignResponse;
import com.tujuhsembilan.glucoseclamp.model.VitalSign;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import com.tujuhsembilan.glucoseclamp.repository.VitalSignRepository;
import com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VitalSignsService {

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllVitalSigns(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<VitalSign> result = vitalSignRepository.findAllActive(pageable);

        List<VitalSignResponse> content = result.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("content", content);
        pageData.put("pageNumber", result.getNumber() + 1);
        pageData.put("pageSize", result.getSize());
        pageData.put("totalElements", result.getTotalElements());
        pageData.put("totalPages", result.getTotalPages());

        return ApiDataResponseBuilder.builder()
            .data(pageData)
                .message("Berhasil mendapatkan data tanda vital")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getVitalSignById(Integer id) {
        VitalSign vital = vitalSignRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (vital == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Berhasil mendapatkan data tanda vital")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder addVitalSign(VitalSignRequest request) {
        VitalSign vital = new VitalSign();

        // session
        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(vital::setSession);

        // measuredAt parsing
        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt;
                if (s.contains("T")) dt = LocalDateTime.parse(s);
                else dt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                vital.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        vital.setSystolic(request.getSystolic());
        vital.setDiastolic(request.getDiastolic());
        vital.setPulse(request.getPulse());
        vital.setRespiratoryRate(request.getRespiratoryRate());
        if (request.getTemperatureC() != null) vital.setTemperatureC(request.getTemperatureC());
        if (request.getSpo2() != null) vital.setSpo2(request.getSpo2());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(vital::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();
        vital.setCreatedAt(now);
        vital.setUpdatedAt(now);
        vital.setCreatedBy(currentUser);
        vital.setUpdatedBy(currentUser);
        vital.setStatus(EntityStatus.ACTIVE);

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Vital sign berhasil ditambahkan")
            .statusCode(HttpStatus.CREATED.value())
            .status(HttpStatus.CREATED)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateVitalSign(Integer id, VitalSignUpdateRequest request) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt = s.contains("T") ? LocalDateTime.parse(s) : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                vital.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        if (request.getSystolic() != null) vital.setSystolic(request.getSystolic());
        if (request.getDiastolic() != null) vital.setDiastolic(request.getDiastolic());
        if (request.getPulse() != null) vital.setPulse(request.getPulse());
        if (request.getRespiratoryRate() != null) vital.setRespiratoryRate(request.getRespiratoryRate());
        if (request.getTemperatureC() != null) vital.setTemperatureC(request.getTemperatureC());
        if (request.getSpo2() != null) vital.setSpo2(request.getSpo2());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(vital::setAssignedByUser);

        vital.setUpdatedBy(currentUser);
        vital.setUpdatedAt(LocalDateTime.now());

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Vital sign berhasil diupdate")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteVitalSign(Integer id) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        vital.setDeletedAt(LocalDateTime.now());
        vital.setDeletedBy(currentUser);
        vital.setStatus(EntityStatus.DELETED);

        vitalSignRepository.save(vital);
        log.info("VitalSign {} berhasil dihapus (soft) oleh user {}", id, currentUser);

        return ApiDataResponseBuilder.builder()
            .message("Vital sign berhasil dihapus")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateVitalSignStatus(Integer id, VitalSignStatusUpdateRequest request) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        vital.setStatus(request.getStatus());
        vital.setUpdatedBy(currentUser);
        vital.setUpdatedAt(LocalDateTime.now());

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Status vital sign berhasil diupdate")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    public ApiDataResponseBuilder searchVitalSigns(String keyword) {
        List<VitalSign> all = vitalSignRepository.findAllActive();
        if (keyword == null || keyword.isBlank()) {
            List<VitalSignResponse> data = all.stream().map(this::mapToResponse).collect(Collectors.toList());
            return ApiDataResponseBuilder.builder()
                .data(data)
                .message("Berhasil mencari tanda vital")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
        }

        String kw = keyword.toLowerCase();
        List<VitalSign> filtered = all.stream().filter(v -> {
            try {
                return (v.getSystolic() != null && v.getSystolic().toString().contains(kw)) ||
                        (v.getDiastolic() != null && v.getDiastolic().toString().contains(kw)) ||
                        (v.getPulse() != null && v.getPulse().toString().contains(kw));
            } catch (Exception ex) {
                return false;
            }
        }).collect(Collectors.toList());

        List<VitalSignResponse> data = filtered.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(data)
                .message("Berhasil mencari tanda vital")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private VitalSignResponse mapToResponse(VitalSign vital) {
        return VitalSignResponse.builder()
                .vitalId(vital.getVitalId())
                .sessionId(vital.getSession() == null ? null : vital.getSession().getSessionId())
                .measuredAt(vital.getMeasuredAt())
                .systolic(vital.getSystolic())
                .diastolic(vital.getDiastolic())
                .pulse(vital.getPulse())
                .respiratoryRate(vital.getRespiratoryRate())
                .temperatureC(vital.getTemperatureC())
                .spo2(vital.getSpo2())
                .assignedBy(vital.getAssignedByUser() == null ? null : vital.getAssignedByUser().getUserId())
                .createdAt(vital.getCreatedAt())
                .createdBy(vital.getCreatedBy())
                .updatedAt(vital.getUpdatedAt())
                .updatedBy(vital.getUpdatedBy())
                .deletedAt(vital.getDeletedAt())
                .deletedBy(vital.getDeletedBy())
                .status(vital.getStatus())
                .build();
    }
}
