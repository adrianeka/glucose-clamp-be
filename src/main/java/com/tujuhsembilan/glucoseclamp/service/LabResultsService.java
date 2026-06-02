package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.LabResultRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.LabResultResponse;
import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import com.tujuhsembilan.glucoseclamp.model.LabResult;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.BloodSampleRepository;
import com.tujuhsembilan.glucoseclamp.repository.LabResultRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
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
public class LabResultsService {

    @Autowired
    private LabResultRepository labResultRepository;

    @Autowired
    private BloodSampleRepository bloodSampleRepository;

    @Autowired
    private UserRepository userRepository;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImplement)) {
            return 1; // Default fallback to system admin ID
        }
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllLabResults(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<LabResult> result = labResultRepository.findAllActive(pageable);

        List<LabResultResponse> content = result.getContent().stream()
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
                .message("Berhasil mendapatkan data lab result")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getLabResultById(String id) {
        LabResult labResult = labResultRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (labResult == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data lab result tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(labResult))
                .message("Berhasil mendapatkan data lab result")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addLabResult(LabResultRequest request) {
        if (labResultRepository.findById(request.getLabResultId()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Lab Result ID sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<BloodSample> bloodSampleOpt = bloodSampleRepository.findByBloodSampleIdAndDeletedAtIsNull(request.getBloodSampleId());
        if (bloodSampleOpt.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Blood Sample tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<LabResult> existingLabResult = labResultRepository.findByBloodSampleIdAndDeletedAtIsNull(request.getBloodSampleId());
        if (existingLabResult.isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Blood Sample sudah memiliki Lab Result")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        User verifier = null;
        if (request.getVerifiedBy() != null) {
            Optional<User> verifierOpt = userRepository.findById(request.getVerifiedBy());
            if (verifierOpt.isEmpty() || EntityStatus.DELETED.name().equals(verifierOpt.get().getStatus().name())) {
                return ApiDataResponseBuilder.builder()
                        .message("User verifikator tidak ditemukan")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            verifier = verifierOpt.get();
        } else {
            verifier = userRepository.findById(getCurrentUserId()).orElse(null);
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        LabResult labResult = LabResult.builder()
                .labResultId(request.getLabResultId())
                .bloodSample(bloodSampleOpt.get())
                .parameterName(request.getParameterName())
                .verifiedByUser(verifier)
                .value(request.getValue())
                .referenceRangeMin(request.getReferenceRangeMin())
                .referenceRangeMax(request.getReferenceRangeMax())
                .unit(request.getUnit())
                .abnormalFlag(request.getAbnormalFlag())
                .build();

        labResult.setCreatedAt(now);
        labResult.setUpdatedAt(now);
        labResult.setCreatedBy(currentUserId);
        labResult.setUpdatedBy(currentUserId);
        labResult.setStatus(EntityStatus.ACTIVE);

        labResultRepository.save(labResult);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(labResult))
                .message("Lab result berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateLabResult(String id, LabResultRequest request) {
        Optional<LabResult> opt = labResultRepository.findByIdAndDeletedAtIsNull(id);
        if (opt.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data lab result tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        LabResult labResult = opt.get();

        Optional<BloodSample> bloodSampleOpt = bloodSampleRepository.findByBloodSampleIdAndDeletedAtIsNull(request.getBloodSampleId());
        if (bloodSampleOpt.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Blood Sample tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (!labResult.getBloodSample().getBloodSampleId().equals(request.getBloodSampleId())) {
            Optional<LabResult> existingLabResult = labResultRepository.findByBloodSampleIdAndDeletedAtIsNull(request.getBloodSampleId());
            if (existingLabResult.isPresent()) {
                return ApiDataResponseBuilder.builder()
                        .message("Blood Sample sudah memiliki Lab Result")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            labResult.setBloodSample(bloodSampleOpt.get());
        }

        User verifier = null;
        if (request.getVerifiedBy() != null) {
            Optional<User> verifierOpt = userRepository.findById(request.getVerifiedBy());
            if (verifierOpt.isEmpty() || EntityStatus.DELETED.name().equals(verifierOpt.get().getStatus().name())) {
                return ApiDataResponseBuilder.builder()
                        .message("User verifikator tidak ditemukan")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
            verifier = verifierOpt.get();
        }

        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        labResult.setParameterName(request.getParameterName());
        if (verifier != null) {
            labResult.setVerifiedByUser(verifier);
        }
        labResult.setValue(request.getValue());
        labResult.setReferenceRangeMin(request.getReferenceRangeMin());
        labResult.setReferenceRangeMax(request.getReferenceRangeMax());
        labResult.setUnit(request.getUnit());
        labResult.setAbnormalFlag(request.getAbnormalFlag());
        labResult.setUpdatedAt(now);
        labResult.setUpdatedBy(currentUserId);

        labResultRepository.save(labResult);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(labResult))
                .message("Lab result berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateLabResultStatus(String id, String statusStr) {
        Optional<LabResult> opt = labResultRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data lab result tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        EntityStatus newStatus;
        try {
            newStatus = EntityStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid. Harus salah satu dari ACTIVE, INACTIVE, DELETED")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        LabResult labResult = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        labResult.setStatus(newStatus);
        labResult.setUpdatedBy(currentUserId);
        labResult.setUpdatedAt(now);

        if (EntityStatus.DELETED.equals(newStatus)) {
            labResult.setDeletedAt(now);
            labResult.setDeletedBy(currentUserId);
        }

        labResultRepository.save(labResult);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(labResult))
                .message("Status lab result berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteLabResult(String id) {
        Optional<LabResult> opt = labResultRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data lab result tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        LabResult labResult = opt.get();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        labResult.setDeletedAt(now);
        labResult.setDeletedBy(currentUserId);
        labResult.setStatus(EntityStatus.DELETED);

        labResultRepository.save(labResult);

        return ApiDataResponseBuilder.builder()
                .message("Lab result berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchLabResults(String search, String startDateStr, String endDateStr) {
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

        List<LabResult> results = labResultRepository.searchLabResults(
                search != null ? search.trim() : null,
                startDate,
                endDate
        );

        List<LabResultResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .data(responseList)
                .message("Berhasil mencari data lab result")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public LabResultResponse mapToResponse(LabResult lr) {
        return LabResultResponse.builder()
                .labResultId(lr.getLabResultId())
                .bloodSampleId(lr.getBloodSample() != null ? lr.getBloodSample().getBloodSampleId() : null)
                .parameterName(lr.getParameterName())
                .verifiedBy(lr.getVerifiedByUser() != null ? lr.getVerifiedByUser().getUserId() : null)
                .value(lr.getValue())
                .referenceRangeMin(lr.getReferenceRangeMin())
                .referenceRangeMax(lr.getReferenceRangeMax())
                .unit(lr.getUnit())
                .abnormalFlag(lr.getAbnormalFlag())
                .createdAt(lr.getCreatedAt())
                .createdBy(lr.getCreatedBy())
                .updatedAt(lr.getUpdatedAt())
                .updatedBy(lr.getUpdatedBy())
                .deletedAt(lr.getDeletedAt())
                .deletedBy(lr.getDeletedBy())
                .status(lr.getStatus() != null ? lr.getStatus().name() : null)
                .build();
    }
}
