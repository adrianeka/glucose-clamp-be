package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.SamplingScheduleRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.SamplingScheduleResponse;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.SamplingSchedule;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.SamplingScheduleRepository;
import com.tujuhsembilan.glucoseclamp.repository.ProtocolRepository;
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
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SamplingSchedulesService {

    @Autowired
    private SamplingScheduleRepository samplingScheduleRepository;

    @Autowired
    private ProtocolRepository protocolRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImplement)) {
            return 1; // Default fallback to system admin ID
        }
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllSamplingSchedules(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<SamplingSchedule> result = samplingScheduleRepository.findAllActive(pageable);

        List<SamplingScheduleResponse> content = result.getContent().stream()
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
                .message("Berhasil mendapatkan data detail protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getSamplingScheduleById(Long id) {
        SamplingSchedule detail = samplingScheduleRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (detail == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail sampling schedule tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Berhasil mendapatkan data detail sampling schedule")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getSamplingSchedulesByProtocolId(
        Long protocolId
    ) {

        List<SamplingSchedule> schedules =
                samplingScheduleRepository
                        .findByProtocolProtocolIdAndDeletedAtIsNullOrderByRelativeMinuteAsc(protocolId);

        List<SamplingScheduleResponse> responses =
                schedules.stream()
                        .map(this::mapToResponse)
                        .toList();

        return ApiDataResponseBuilder.builder()
                .data(responses)
                .message("Berhasil mendapatkan data detail sampling schedule")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addSamplingSchedule(SamplingScheduleRequest request) {
        // if (samplingScheduleRepository.findById(request.getSamplingScheduleId()).isPresent()) {
        //     return ApiDataResponseBuilder.builder()
        //             .message("Sampling Schedule ID sudah digunakan")
        //             .statusCode(HttpStatus.BAD_REQUEST.value())
        //             .status(HttpStatus.BAD_REQUEST)
        //             .build();
        // }

        Optional<Protocol> protocolOpt = protocolRepository.findById(request.getProtocolId());
        if (protocolOpt.isEmpty() || EntityStatus.DELETED.equals(protocolOpt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer relativeMinute = 0;

        SamplingSchedule lastSchedule =
                samplingScheduleRepository
                        .findTopByProtocolProtocolIdAndStatusOrderByRelativeMinuteDesc(
                                request.getProtocolId(),
                                EntityStatus.ACTIVE
                        )
                        .orElse(null);

        if (lastSchedule != null) {
            relativeMinute =
                    lastSchedule.getRelativeMinute()
                    + request.getTimeInterval();
        } else {
            relativeMinute = request.getTimeInterval();
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        SamplingSchedule detail = SamplingSchedule.builder()
                .protocol(protocolOpt.get())
                .phaseCode(request.getPhaseCode())
                .timeInterval(request.getTimeInterval())
                .relativeMinute(relativeMinute)
                .bloodRaw(request.getBloodRaw())
                .insulinInject(request.getInsulinInject())
                .pkSampleCollection(request.getPkSampleCollection())
                .build();

        detail.setCreatedAt(now);
        detail.setUpdatedAt(now);
        detail.setCreatedBy(currentUserId);
        detail.setUpdatedBy(currentUserId);
        detail.setStatus(EntityStatus.ACTIVE);

        samplingScheduleRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Detail sampling schedule berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateSamplingSchedule(Long id, SamplingScheduleRequest request) {

        Optional<SamplingSchedule> opt = samplingScheduleRepository.findById(id);

        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail sampling schedule tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        SamplingSchedule detail = opt.get();

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        boolean intervalChanged = false;

        if (request.getProtocolId() != null) {
            Optional<Protocol> protocolOpt =
                    protocolRepository.findById(request.getProtocolId());

            if (protocolOpt.isEmpty()
                    || EntityStatus.DELETED.equals(protocolOpt.get().getStatus())) {

                return ApiDataResponseBuilder.builder()
                        .message("Protocol tidak ditemukan")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }

            detail.setProtocol(protocolOpt.get());
        }

        if (request.getPhaseCode() != null) {
            detail.setPhaseCode(request.getPhaseCode());
        }

        if (request.getTimeInterval() != null) {

            if (!request.getTimeInterval().equals(detail.getTimeInterval())) {
                intervalChanged = true;
            }

            detail.setTimeInterval(request.getTimeInterval());
        }

        if (request.getBloodRaw() != null) {
            detail.setBloodRaw(request.getBloodRaw());
        }

        if (request.getInsulinInject() != null) {
            detail.setInsulinInject(request.getInsulinInject());
        }

        if (request.getPkSampleCollection() != null) {
            detail.setPkSampleCollection(request.getPkSampleCollection());
        }

        detail.setUpdatedBy(currentUserId);
        detail.setUpdatedAt(now);

        samplingScheduleRepository.save(detail);

        if (intervalChanged) {
            recalculateRelativeMinute(
                    detail.getProtocol().getProtocolId()
            );
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Detail sampling schedule berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateSamplingScheduleStatus(Long id, String statusStr) {
        Optional<SamplingSchedule> opt = samplingScheduleRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail sampling schedule tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        EntityStatus newStatus;
        try {
            newStatus = EntityStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (EntityStatus.DELETED.equals(newStatus)) {
            return ApiDataResponseBuilder.builder()
                    .message("Gunakan endpoint DELETE untuk menghapus data")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        SamplingSchedule detail = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        detail.setStatus(newStatus);
        detail.setUpdatedBy(currentUserId);
        detail.setUpdatedAt(now);

        samplingScheduleRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Status detail sampling schedule berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteSamplingSchedule(Long id) {
        Optional<SamplingSchedule> opt = samplingScheduleRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        SamplingSchedule detail = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        detail.setDeletedAt(now);
        detail.setDeletedBy(currentUserId);
        detail.setStatus(EntityStatus.DELETED);

        samplingScheduleRepository.save(detail);

        recalculateRelativeMinute(
                detail.getProtocol().getProtocolId()
        );

        return ApiDataResponseBuilder.builder()
                .message("Detail sampling schedule berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private void recalculateRelativeMinute(Long protocolId) {

        List<SamplingSchedule> schedules =
                samplingScheduleRepository
                        .findByProtocolProtocolIdAndDeletedAtIsNullOrderByRelativeMinuteAsc(protocolId);

        int currentMinute = 0;

        for (int i = 0; i < schedules.size(); i++) {

            SamplingSchedule schedule = schedules.get(i);

            if (i == 0) {
                currentMinute = schedule.getTimeInterval() != null
                        ? schedule.getTimeInterval()
                        : 0;

                schedule.setRelativeMinute(currentMinute);
            } else {
                currentMinute += schedule.getTimeInterval();
                schedule.setRelativeMinute(currentMinute);
            }
        }

        samplingScheduleRepository.saveAll(schedules);
    }

    public ApiDataResponseBuilder searchSamplingSchedules(Long protocolId, String search, String startDateStr, String endDateStr) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = startDateStr.contains("T") ? LocalDateTime.parse(startDateStr) : LocalDateTime.parse(startDateStr + "T00:00:00");
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = endDateStr.contains("T") ? LocalDateTime.parse(endDateStr) : LocalDateTime.parse(endDateStr + "T23:59:59.999999999");
            }
        } catch (DateTimeParseException ex) {
            log.error("Failed to parse search dates: {} - {}", startDateStr, endDateStr, ex);
        }

        List<SamplingSchedule> results = samplingScheduleRepository.searchSamplingSchedules(
                protocolId,
                search != null ? search.trim() : null,
                startDate,
                endDate
        );

        List<SamplingScheduleResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(responseList)
                .message("Berhasil mencari data detail sampling schedule")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public SamplingScheduleResponse mapToResponse(SamplingSchedule detail) {
        return SamplingScheduleResponse.builder()
                .samplingScheduleId(detail.getSamplingScheduleId())
                .protocolId(detail.getProtocol() != null ? detail.getProtocol().getProtocolId() : null)
                .phaseCode(detail.getPhaseCode())
                .timeInterval(detail.getTimeInterval())
                .relativeMinute(detail.getRelativeMinute())
                .bloodRaw(detail.getBloodRaw())
                .insulinInject(detail.getInsulinInject())
                .pkSampleCollection(detail.getPkSampleCollection())
                .createdAt(detail.getCreatedAt())
                .createdBy(detail.getCreatedBy())
                .updatedAt(detail.getUpdatedAt())
                .updatedBy(detail.getUpdatedBy())
                .deletedAt(detail.getDeletedAt())
                .deletedBy(detail.getDeletedBy())
                .status(detail.getStatus() != null ? detail.getStatus().name() : null)
                .build();
    }
}
