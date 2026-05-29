package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolDetailRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.ProtocolDetailResponse;
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
public class ProtocolDetailsService {

    @Autowired
    private ProtocolDetailRepository protocolDetailRepository;

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

    public ApiDataResponseBuilder getAllProtocolDetails(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<ProtocolDetail> result = protocolDetailRepository.findAllActive(pageable);

        List<ProtocolDetailResponse> content = result.getContent().stream()
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

    public ApiDataResponseBuilder getProtocolDetailById(String id) {
        ProtocolDetail detail = protocolDetailRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (detail == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Berhasil mendapatkan data detail protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addProtocolDetail(ProtocolDetailRequest request) {
        if (protocolDetailRepository.findById(request.getProtocolsDetailId()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol Detail ID sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Protocol> protocolOpt = protocolRepository.findById(request.getProtocolId());
        if (protocolOpt.isEmpty() || EntityStatus.DELETED.equals(protocolOpt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Protocol tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        ProtocolDetail detail = ProtocolDetail.builder()
                .protocolDetailId(request.getProtocolsDetailId())
                .protocol(protocolOpt.get())
                .phaseCode(request.getPhaseCode())
                .timeInterval(request.getTimeInterval())
                .bloodRaw(request.getBloodRaw())
                .insulinInject(request.getInsulinInject())
                .insulinCheck(request.getInsulinCheck())
                .build();

        detail.setCreatedAt(now);
        detail.setUpdatedAt(now);
        detail.setCreatedBy(currentUserId);
        detail.setUpdatedBy(currentUserId);
        detail.setStatus(EntityStatus.ACTIVE);

        protocolDetailRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Detail protocol berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateProtocolDetail(String id, ProtocolDetailRequest request) {
        Optional<ProtocolDetail> opt = protocolDetailRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        ProtocolDetail detail = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        if (request.getProtocolId() != null) {
            Optional<Protocol> protocolOpt = protocolRepository.findById(request.getProtocolId());
            if (protocolOpt.isEmpty() || EntityStatus.DELETED.equals(protocolOpt.get().getStatus())) {
                return ApiDataResponseBuilder.builder()
                        .message("Protocol tidak ditemukan")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            detail.setProtocol(protocolOpt.get());
        }

        if (request.getPhaseCode() != null) detail.setPhaseCode(request.getPhaseCode());
        if (request.getTimeInterval() != null) detail.setTimeInterval(request.getTimeInterval());
        if (request.getBloodRaw() != null) detail.setBloodRaw(request.getBloodRaw());
        if (request.getInsulinInject() != null) detail.setInsulinInject(request.getInsulinInject());
        if (request.getInsulinCheck() != null) detail.setInsulinCheck(request.getInsulinCheck());

        detail.setUpdatedBy(currentUserId);
        detail.setUpdatedAt(now);

        protocolDetailRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Detail protocol berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateProtocolDetailStatus(String id, String statusStr) {
        Optional<ProtocolDetail> opt = protocolDetailRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail protocol tidak ditemukan")
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

        ProtocolDetail detail = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        detail.setStatus(newStatus);
        detail.setUpdatedBy(currentUserId);
        detail.setUpdatedAt(now);

        protocolDetailRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(detail))
                .message("Status detail protocol berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteProtocolDetail(String id) {
        Optional<ProtocolDetail> opt = protocolDetailRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data detail protocol tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        ProtocolDetail detail = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        detail.setDeletedAt(now);
        detail.setDeletedBy(currentUserId);
        detail.setStatus(EntityStatus.DELETED);

        protocolDetailRepository.save(detail);

        return ApiDataResponseBuilder.builder()
                .message("Detail protocol berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchProtocolDetails(String protocolId, String search, String startDateStr, String endDateStr) {
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

        List<ProtocolDetail> results = protocolDetailRepository.searchProtocolDetails(
                protocolId != null ? protocolId.trim() : null,
                search != null ? search.trim() : null,
                startDate,
                endDate
        );

        List<ProtocolDetailResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(responseList)
                .message("Berhasil mencari data detail protocol")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ProtocolDetailResponse mapToResponse(ProtocolDetail detail) {
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
