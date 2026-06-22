package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.InfusionMonitoringResponse;
import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.InfusionMonitoringRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfusionMonitoringService {

    private final InfusionMonitoringRepository infusionMonitoringRepository;
    private final SessionRepository sessionRepository;
    private final ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        try {
            var principal = authentication.getPrincipal();
            var userDetails = (com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement) principal;
            return userDetails.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    public ApiDataResponseBuilder getAllInfusionMonitorings(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<InfusionMonitoringResponse> result = infusionMonitoringRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data infusion monitorings")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getInfusionMonitoringById(String id) {
        Optional<InfusionMonitoring> opt = infusionMonitoringRepository.findByIdAndDeletedAtIsNull(id);
        if (opt.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data infusion monitoring tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(opt.get()))
                .message("Berhasil mendapatkan data infusion monitoring")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addInfusionMonitoring(InfusionMonitoringRequest request) {
        Session session = sessionRepository.findById(request.getSessionId()).orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        InfusionMonitoring im = new InfusionMonitoring();
        im.setInfusionId(nextInfusionId());
        im.setSession(session);
        try {
            if (request.getTime() != null) im.setTime(LocalDateTime.parse(request.getTime()));
        } catch (DateTimeParseException ignored) {
        }
        im.setGlucoseValue(request.getGlucoseValue());
        im.setConfirmationRateMinKg(request.getConfirmationRateMinKg());
        im.setRateMinKg(request.getRateMinKg());
        im.setFlowRateMlHr(request.getFlowRateMlHr());
        im.setAdjustmentNote(request.getAdjustmentNote());
        im.setMonitoredBy(request.getMonitoredBy());

        Integer uid = getCurrentUserId();
        im.setCreatedBy(uid);
        im.setUpdatedBy(uid);
        im.setStatus(EntityStatus.ACTIVE);

        infusionMonitoringRepository.save(im);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(im))
                .message("Infusion monitoring berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateInfusionMonitoring(String id, InfusionMonitoringUpdateRequest request) {
        Optional<InfusionMonitoring> opt = infusionMonitoringRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data infusion monitoring tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        InfusionMonitoring im = opt.get();
        try {
            if (request.getTime() != null) im.setTime(LocalDateTime.parse(request.getTime()));
        } catch (DateTimeParseException ignored) {
        }
        if (request.getGlucoseValue() != null) im.setGlucoseValue(request.getGlucoseValue());
        if (request.getConfirmationRateMinKg() != null) im.setConfirmationRateMinKg(request.getConfirmationRateMinKg());
        if (request.getRateMinKg() != null) im.setRateMinKg(request.getRateMinKg());
        if (request.getFlowRateMlHr() != null) im.setFlowRateMlHr(request.getFlowRateMlHr());
        if (request.getAdjustmentNote() != null) im.setAdjustmentNote(request.getAdjustmentNote());
        if (request.getMonitoredBy() != null) im.setMonitoredBy(request.getMonitoredBy());

        Integer uid = getCurrentUserId();
        im.setUpdatedBy(uid);
        infusionMonitoringRepository.save(im);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(im))
                .message("Infusion monitoring berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteInfusionMonitoring(String id) {
        Optional<InfusionMonitoring> opt = infusionMonitoringRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data infusion monitoring tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        InfusionMonitoring im = opt.get();
        Integer uid = getCurrentUserId();
        im.setDeletedAt(LocalDateTime.now());
        im.setDeletedBy(uid);
        im.setStatus(EntityStatus.DELETED);
        im.setUpdatedBy(uid);
        infusionMonitoringRepository.save(im);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(im))
                .message("Infusion monitoring berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateInfusionMonitoringStatus(String id, InfusionMonitoringStatusUpdateRequest request) {
        Optional<InfusionMonitoring> opt = infusionMonitoringRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data infusion monitoring tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        InfusionMonitoring im = opt.get();
        Integer uid = getCurrentUserId();
        im.setStatus(request.getStatus());
        im.setUpdatedBy(uid);
        im.setUpdatedAt(LocalDateTime.now());
        if (EntityStatus.DELETED.equals(request.getStatus())) {
            im.setDeletedAt(LocalDateTime.now());
            im.setDeletedBy(uid);
        }
        infusionMonitoringRepository.save(im);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(im))
                .message("Status infusion monitoring berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchInfusionMonitorings(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<InfusionMonitoringResponse> result;
        if (keyword == null || keyword.isBlank()) {
            result = infusionMonitoringRepository.findAllActive(pageable).map(this::mapToResponse);
        } else {
            result = infusionMonitoringRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);
        }

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari infusion monitorings")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private InfusionMonitoringResponse mapToResponse(InfusionMonitoring im) {
        InfusionMonitoringResponse resp = modelMapper.map(im, InfusionMonitoringResponse.class);
        resp.setStatus(im.getStatus() == null ? null : im.getStatus().name());
        resp.setSessionId(im.getSession() == null ? null : im.getSession().getSessionId());
        return resp;
    }

    private String nextInfusionId() {
        Optional<InfusionMonitoring> lastOpt = infusionMonitoringRepository.findTopByDeletedAtIsNullOrderByInfusionIdDesc();
        if (lastOpt.isEmpty() || lastOpt.get().getInfusionId() == null || lastOpt.get().getInfusionId().isBlank()) {
            return "INF-001";
        }

        String lastId = lastOpt.get().getInfusionId().trim();
        if (!lastId.startsWith("INF-")) {
            return "INF-001";
        }

        try {
            int sequence = Integer.parseInt(lastId.substring(4));
            return String.format("INF-%03d", sequence + 1);
        } catch (NumberFormatException ex) {
            return "INF-001";
        }
    }
}
