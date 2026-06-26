package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.SamplingScheduleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolDropdownProjection;
import com.tujuhsembilan.glucoseclamp.dto.response.SamplingScheduleResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolResponseDetail;
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
import java.util.Objects;

@Slf4j
@Service
public class ProtocolsService {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private SamplingScheduleRepository samplingScheduleRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImplement)) {
            return 1; // Default fallback to system admin ID
        }
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllProtocols(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<Protocol> result = protocolRepository.findAllActive(pageable);

        List<ProtocolResponse> content = result.getContent().stream()
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
                .message("Berhasil mendapatkan data protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getProtocolsDropdown() {
        try {
            List<ProtocolDropdownProjection> protocols = protocolRepository.findAllDropdown();
            return ApiDataResponseBuilder.builder()
                    .status(HttpStatus.OK)
                    .data(protocols)
                    .build();
        } catch (Exception e) {
            return ApiDataResponseBuilder.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Error fetching dropdown: " + e.getMessage())
                    .build();
        }
    }

    public ApiDataResponseBuilder getProtocolById(Long id) {
        Protocol protocol = protocolRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (protocol == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponseDetailProtocol(protocol))
                .message("Berhasil mendapatkan data protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addProtocol(ProtocolRequest request) {
        if (protocolRepository.findByProtocolCode(request.getProtocolCode()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol Code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        // Ditambahkan mapping untuk 3 field baru
        Protocol protocol = Protocol.builder()
                .protocolCode(request.getProtocolCode())
                .protocolName(request.getProtocolName())
                .insulinDoseRule(request.getInsulinDoseRule())
                .insulinDoseUnit(request.getInsulinDoseUnit())
                .glucoseTargetMin(request.getGlucoseTargetMin())
                .glucoseTargetMax(request.getGlucoseTargetMax())
                .glucoseTargetUnit(request.getGlucoseTargetUnit())
                .glucoseTargetMinExtreme(request.getGlucoseTargetMinExtreme())
                .glucoseTargetMaxExtreme(request.getGlucoseTargetMaxExtreme())
                .durationHours(request.getDurationHours())
                .glucoseDropTriggerPercentage(request.getGlucoseDropTriggerPercentage())
                .initialGlucoseInfusionRate(request.getInitialGlucoseInfusionRate())
                .initialGlucoseInfusionRateUnit(request.getInitialGlucoseInfusionRateUnit())
                .version(request.getVersion())
                .build();

        protocol.setCreatedAt(now);
        protocol.setUpdatedAt(now);
        protocol.setCreatedBy(currentUserId);
        protocol.setUpdatedBy(currentUserId);
        protocol.setStatus(EntityStatus.ACTIVE);

        Protocol data = protocolRepository.save(protocol);

        if (request.getSamplingSchedules() != null) {
            for (SamplingScheduleRequest detailReq : request.getSamplingSchedules()) {
                SamplingSchedule detail = SamplingSchedule.builder()
                        .protocol(protocol)
                        .phaseCode(detailReq.getPhaseCode())
                        .timeInterval(detailReq.getTimeInterval())
                        .bloodRaw(detailReq.getBloodRaw())
                        .insulinInject(detailReq.getInsulinInject())
                        .pkSampleCollection(detailReq.getPkSampleCollection())
                        .build();

                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);
                detail.setCreatedBy(currentUserId);
                detail.setUpdatedBy(currentUserId);
                detail.setStatus(EntityStatus.ACTIVE);

                samplingScheduleRepository.save(detail);
            }
        }

        Protocol saved = protocolRepository.findById(data.getProtocolId()).orElse(protocol);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(saved))
                .message("Protocol berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateProtocol(Long id, ProtocolRequest request) {
        Optional<Protocol> opt = protocolRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Protocol protocol = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        if (request.getProtocolCode() != null && !request.getProtocolCode().equals(protocol.getProtocolCode())) {
            if (protocolRepository.findByProtocolCode(request.getProtocolCode()).isPresent()) {
                return ApiDataResponseBuilder.builder()
                        .message("Protocol Code sudah digunakan")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            protocol.setProtocolCode(request.getProtocolCode());
        }

        if (request.getProtocolName() != null) protocol.setProtocolName(request.getProtocolName());
        if (request.getInsulinDoseRule() != null) protocol.setInsulinDoseRule(request.getInsulinDoseRule());
        if (request.getInsulinDoseUnit() != null) protocol.setInsulinDoseUnit(request.getInsulinDoseUnit());
        if (request.getGlucoseTargetMin() != null) protocol.setGlucoseTargetMin(request.getGlucoseTargetMin());
        if (request.getGlucoseTargetMax() != null) protocol.setGlucoseTargetMax(request.getGlucoseTargetMax());
        if (request.getGlucoseTargetUnit() != null) protocol.setGlucoseTargetUnit(request.getGlucoseTargetUnit());
        if (request.getGlucoseTargetMinExtreme() != null) protocol.setGlucoseTargetMinExtreme(request.getGlucoseTargetMinExtreme());
        if (request.getGlucoseTargetMaxExtreme() != null) protocol.setGlucoseTargetMaxExtreme(request.getGlucoseTargetMaxExtreme());
        if (request.getDurationHours() != null) protocol.setDurationHours(request.getDurationHours());
        
        // Ditambahkan pengecekan & pembaruan nilai untuk 3 field baru
        if (request.getGlucoseDropTriggerPercentage() != null) protocol.setGlucoseDropTriggerPercentage(request.getGlucoseDropTriggerPercentage());
        if (request.getInitialGlucoseInfusionRate() != null) protocol.setInitialGlucoseInfusionRate(request.getInitialGlucoseInfusionRate());
        if (request.getInitialGlucoseInfusionRateUnit() != null) protocol.setInitialGlucoseInfusionRateUnit(request.getInitialGlucoseInfusionRateUnit());
        
        if (request.getVersion() != null) protocol.setVersion(request.getVersion());

        protocol.setUpdatedBy(currentUserId);
        protocol.setUpdatedAt(now);

        protocolRepository.save(protocol);

        if (request.getSamplingSchedules() != null) {
            List<SamplingSchedule> currentDetails = samplingScheduleRepository.findByProtocolIdAndDeletedAtIsNull(protocol.getProtocolId());
            for (SamplingSchedule cd : currentDetails) {
                cd.setDeletedAt(now);
                cd.setDeletedBy(currentUserId);
                cd.setStatus(EntityStatus.DELETED);
                samplingScheduleRepository.save(cd);
            }

            for (SamplingScheduleRequest detailReq : request.getSamplingSchedules()) {
                SamplingSchedule detail = SamplingSchedule.builder()
                        .protocol(protocol)
                        .phaseCode(detailReq.getPhaseCode())
                        .timeInterval(detailReq.getTimeInterval())
                        .bloodRaw(detailReq.getBloodRaw())
                        .insulinInject(detailReq.getInsulinInject())
                        .pkSampleCollection(detailReq.getPkSampleCollection())
                        .build();

                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);
                detail.setCreatedBy(currentUserId);
                detail.setUpdatedBy(currentUserId);
                detail.setStatus(EntityStatus.ACTIVE);

                samplingScheduleRepository.save(detail);
            }
        }

        Protocol updated = protocolRepository.findById(id).orElse(protocol);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(updated))
                .message("Protocol berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateProtocolStatus(Long id, String statusStr) {
        Optional<Protocol> opt = protocolRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data protocol tidak ditemukan")
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

        Protocol protocol = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        protocol.setStatus(newStatus);
        protocol.setUpdatedBy(currentUserId);
        protocol.setUpdatedAt(now);

        protocolRepository.save(protocol);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(protocol))
                .message("Status protocol berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteProtocol(Long id) {
        Optional<Protocol> opt = protocolRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Protocol protocol = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        protocol.setDeletedAt(now);
        protocol.setDeletedBy(currentUserId);
        protocol.setStatus(EntityStatus.DELETED);

        protocolRepository.save(protocol);

        List<SamplingSchedule> details = samplingScheduleRepository.findByProtocolIdAndDeletedAtIsNull(protocol.getProtocolId());
        for (SamplingSchedule detail : details) {
            detail.setDeletedAt(now);
            detail.setDeletedBy(currentUserId);
            detail.setStatus(EntityStatus.DELETED);
            samplingScheduleRepository.save(detail);
        }

        return ApiDataResponseBuilder.builder()
                .message("Protocol berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchProtocols(String search, String startDateStr, String endDateStr) {
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

        List<Protocol> results = protocolRepository.searchProtocols(
                search != null ? search.trim() : null,
                startDate,
                endDate
        );

        List<ProtocolResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(responseList)
                .message("Berhasil mencari data protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ProtocolResponse mapToResponse(Protocol protocol) {
        // Ditambahkan pemetaan untuk 3 field baru ke DTO ProtocolResponse
        return ProtocolResponse.builder()
                .protocolId(protocol.getProtocolId())
                .protocolCode(protocol.getProtocolCode())
                .protocolName(protocol.getProtocolName())
                .insulinDoseRule(protocol.getInsulinDoseRule())
                .insulinDoseUnit(protocol.getInsulinDoseUnit())
                .glucoseTargetMin(protocol.getGlucoseTargetMin())
                .glucoseTargetMax(protocol.getGlucoseTargetMax())
                .glucoseTargetUnit(protocol.getGlucoseTargetUnit())
                .glucoseTargetMinExtreme(protocol.getGlucoseTargetMinExtreme())
                .glucoseTargetMaxExtreme(protocol.getGlucoseTargetMaxExtreme())
                .durationHours(protocol.getDurationHours())
                .glucoseDropTriggerPercentage(protocol.getGlucoseDropTriggerPercentage())
                .initialGlucoseInfusionRate(protocol.getInitialGlucoseInfusionRate())
                .initialGlucoseInfusionRateUnit(protocol.getInitialGlucoseInfusionRateUnit())
                .version(protocol.getVersion())
                .createdAt(protocol.getCreatedAt())
                .createdBy(protocol.getCreatedBy())
                .updatedAt(protocol.getUpdatedAt())
                .updatedBy(protocol.getUpdatedBy())
                .deletedAt(protocol.getDeletedAt())
                .deletedBy(protocol.getDeletedBy())
                .status(protocol.getStatus() != null ? protocol.getStatus().name() : null)
                .samplingScheduleSummary(buildSamplingScheduleSummary(protocol))
                .build();
    }

    public ProtocolResponseDetail mapToResponseDetailProtocol(Protocol protocol) {
        List<SamplingScheduleResponse> details = null;
        if (protocol.getSamplingSchedules() != null) {
            details = protocol.getSamplingSchedules().stream()
                    .filter(pd -> pd.getDeletedAt() == null)
                    .map(this::mapDetailToResponse)
                    .collect(Collectors.toList());
        }

        // Ditambahkan pemetaan untuk 3 field baru ke DTO ProtocolResponseDetail
        return ProtocolResponseDetail.builder()
                .protocolId(protocol.getProtocolId())
                .protocolCode(protocol.getProtocolCode())
                .protocolName(protocol.getProtocolName())
                .insulinDoseRule(protocol.getInsulinDoseRule())
                .insulinDoseUnit(protocol.getInsulinDoseUnit())
                .glucoseTargetMin(protocol.getGlucoseTargetMin())
                .glucoseTargetMax(protocol.getGlucoseTargetMax())
                .glucoseTargetUnit(protocol.getGlucoseTargetUnit())
                .glucoseTargetMinExtreme(protocol.getGlucoseTargetMinExtreme())
                .glucoseTargetMaxExtreme(protocol.getGlucoseTargetMaxExtreme())
                .durationHours(protocol.getDurationHours())
                .glucoseDropTriggerPercentage(protocol.getGlucoseDropTriggerPercentage())
                .initialGlucoseInfusionRate(protocol.getInitialGlucoseInfusionRate())
                .initialGlucoseInfusionRateUnit(protocol.getInitialGlucoseInfusionRateUnit())
                .version(protocol.getVersion())
                .createdAt(protocol.getCreatedAt())
                .createdBy(protocol.getCreatedBy())
                .updatedAt(protocol.getUpdatedAt())
                .updatedBy(protocol.getUpdatedBy())
                .deletedAt(protocol.getDeletedAt())
                .deletedBy(protocol.getDeletedBy())
                .status(protocol.getStatus() != null ? protocol.getStatus().name() : null)
                .samplingSchedules(details)
                .build();
    }

    public SamplingScheduleResponse mapDetailToResponse(SamplingSchedule detail) {
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

    private String buildSamplingScheduleSummary(Protocol protocol) {
        if (protocol.getSamplingSchedules() == null) {
            return "0 phase";
        }

        List<SamplingSchedule> schedules = protocol.getSamplingSchedules()
                .stream()
                .filter(s -> s.getDeletedAt() == null)
                .toList();

        int phaseCount = schedules.size();

        if (phaseCount == 0) {
            return "0 phase";
        }

        Integer maxMinute = schedules.stream()
                .map(SamplingSchedule::getRelativeMinute)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        String durationText;

        if (maxMinute < 60) {
            durationText = maxMinute + " menit";
        } else {
            int hours = maxMinute / 60;
            int minutes = maxMinute % 60;

            if (minutes == 0) {
                durationText = hours + " jam";
            } else {
                durationText = hours + " h " + minutes + " m";
            }
        }

        return phaseCount + " phase (" + durationText + ")";
    }
}