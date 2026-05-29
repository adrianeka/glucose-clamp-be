package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolDetailRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolDetailResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolResponse;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.ProtocolDetail;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.ProtocolDetailRepository;
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
public class ProtocolsService {

    @Autowired
    private ProtocolRepository protocolRepository;

    @Autowired
    private ProtocolDetailRepository protocolDetailRepository;

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

    public ApiDataResponseBuilder getProtocolById(String id) {
        Protocol protocol = protocolRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (protocol == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(protocol))
                .message("Berhasil mendapatkan data protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addProtocol(ProtocolRequest request) {
        if (protocolRepository.findById(request.getProtocolId()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol ID sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (protocolRepository.findByProtocolCodeAndDeletedAtIsNull(request.getProtocolCode()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol Code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        Protocol protocol = Protocol.builder()
                .protocolId(request.getProtocolId())
                .protocolCode(request.getProtocolCode())
                .protocolName(request.getProtocolName())
                .insulinDoseRule(request.getInsulinDoseRule())
                .insulinDoseUnit(request.getInsulinDoseUnit())
                .glucoseTargetMin(request.getGlucoseTargetMin())
                .glucoseTargetMax(request.getGlucoseTargetMax())
                .glucoseTargetUnit(request.getGlucoseTargetUnit())
                .durationHours(request.getDurationHours())
                .version(request.getVersion())
                .build();

        protocol.setCreatedAt(now);
        protocol.setUpdatedAt(now);
        protocol.setCreatedBy(currentUserId);
        protocol.setUpdatedBy(currentUserId);
        protocol.setStatus(EntityStatus.ACTIVE);

        protocolRepository.save(protocol);

        if (request.getProtocolDetails() != null) {
            for (ProtocolDetailRequest detailReq : request.getProtocolDetails()) {
                ProtocolDetail detail = ProtocolDetail.builder()
                        .protocolDetailId(detailReq.getProtocolsDetailId())
                        .protocol(protocol)
                        .phaseCode(detailReq.getPhaseCode())
                        .timeInterval(detailReq.getTimeInterval())
                        .bloodRaw(detailReq.getBloodRaw())
                        .insulinInject(detailReq.getInsulinInject())
                        .insulinCheck(detailReq.getInsulinCheck())
                        .build();

                detail.setCreatedAt(now);
                detail.setUpdatedAt(now);
                detail.setCreatedBy(currentUserId);
                detail.setUpdatedBy(currentUserId);
                detail.setStatus(EntityStatus.ACTIVE);

                protocolDetailRepository.save(detail);
            }
        }

        Protocol saved = protocolRepository.findById(protocol.getProtocolId()).orElse(protocol);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(saved))
                .message("Protocol berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateProtocol(String id, ProtocolRequest request) {
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
            if (protocolRepository.findByProtocolCodeAndDeletedAtIsNull(request.getProtocolCode()).isPresent()) {
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
        if (request.getDurationHours() != null) protocol.setDurationHours(request.getDurationHours());
        if (request.getVersion() != null) protocol.setVersion(request.getVersion());

        protocol.setUpdatedBy(currentUserId);
        protocol.setUpdatedAt(now);

        protocolRepository.save(protocol);

        if (request.getProtocolDetails() != null) {
            List<ProtocolDetail> currentDetails = protocolDetailRepository.findByProtocolIdAndDeletedAtIsNull(protocol.getProtocolId());
            for (ProtocolDetail cd : currentDetails) {
                cd.setDeletedAt(now);
                cd.setDeletedBy(currentUserId);
                cd.setStatus(EntityStatus.DELETED);
                protocolDetailRepository.save(cd);
            }

            for (ProtocolDetailRequest detailReq : request.getProtocolDetails()) {
                Optional<ProtocolDetail> existingOpt = protocolDetailRepository.findById(detailReq.getProtocolsDetailId());
                ProtocolDetail detail;
                if (existingOpt.isPresent()) {
                    detail = existingOpt.get();
                    detail.setProtocol(protocol);
                    detail.setPhaseCode(detailReq.getPhaseCode());
                    detail.setTimeInterval(detailReq.getTimeInterval());
                    detail.setBloodRaw(detailReq.getBloodRaw());
                    detail.setInsulinInject(detailReq.getInsulinInject());
                    detail.setInsulinCheck(detailReq.getInsulinCheck());
                    detail.setUpdatedBy(currentUserId);
                    detail.setUpdatedAt(now);
                    detail.setStatus(EntityStatus.ACTIVE);
                    detail.setDeletedAt(null);
                    detail.setDeletedBy(null);
                } else {
                    detail = ProtocolDetail.builder()
                            .protocolDetailId(detailReq.getProtocolsDetailId())
                            .protocol(protocol)
                            .phaseCode(detailReq.getPhaseCode())
                            .timeInterval(detailReq.getTimeInterval())
                            .bloodRaw(detailReq.getBloodRaw())
                            .insulinInject(detailReq.getInsulinInject())
                            .insulinCheck(detailReq.getInsulinCheck())
                            .build();
                    detail.setCreatedAt(now);
                    detail.setUpdatedAt(now);
                    detail.setCreatedBy(currentUserId);
                    detail.setUpdatedBy(currentUserId);
                    detail.setStatus(EntityStatus.ACTIVE);
                }
                protocolDetailRepository.save(detail);
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
    public ApiDataResponseBuilder updateProtocolStatus(String id, String statusStr) {
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
    public ApiDataResponseBuilder deleteProtocol(String id) {
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

        List<ProtocolDetail> details = protocolDetailRepository.findByProtocolIdAndDeletedAtIsNull(protocol.getProtocolId());
        for (ProtocolDetail detail : details) {
            detail.setDeletedAt(now);
            detail.setDeletedBy(currentUserId);
            detail.setStatus(EntityStatus.DELETED);
            protocolDetailRepository.save(detail);
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
        List<ProtocolDetailResponse> details = null;
        if (protocol.getProtocolDetails() != null) {
            details = protocol.getProtocolDetails().stream()
                    .filter(pd -> pd.getDeletedAt() == null)
                    .map(this::mapDetailToResponse)
                    .collect(Collectors.toList());
        }

        return ProtocolResponse.builder()
                .protocolId(protocol.getProtocolId())
                .protocolCode(protocol.getProtocolCode())
                .protocolName(protocol.getProtocolName())
                .insulinDoseRule(protocol.getInsulinDoseRule())
                .insulinDoseUnit(protocol.getInsulinDoseUnit())
                .glucoseTargetMin(protocol.getGlucoseTargetMin())
                .glucoseTargetMax(protocol.getGlucoseTargetMax())
                .glucoseTargetUnit(protocol.getGlucoseTargetUnit())
                .durationHours(protocol.getDurationHours())
                .version(protocol.getVersion())
                .createdAt(protocol.getCreatedAt())
                .createdBy(protocol.getCreatedBy())
                .updatedAt(protocol.getUpdatedAt())
                .updatedBy(protocol.getUpdatedBy())
                .deletedAt(protocol.getDeletedAt())
                .deletedBy(protocol.getDeletedBy())
                .status(protocol.getStatus() != null ? protocol.getStatus().name() : null)
                .protocolDetails(details)
                .build();
    }

    public ProtocolDetailResponse mapDetailToResponse(ProtocolDetail detail) {
        return ProtocolDetailResponse.builder()
                .protocolsDetailId(detail.getProtocolDetailId())
                .protocolId(detail.getProtocol() != null ? detail.getProtocol().getProtocolId() : null)
                .phaseCode(detail.getPhaseCode())
                .timeInterval(detail.getTimeInterval())
                .bloodRaw(detail.getBloodRaw())
                .insulinInject(detail.getInsulinInject())
                .insulinCheck(detail.getInsulinCheck())
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
