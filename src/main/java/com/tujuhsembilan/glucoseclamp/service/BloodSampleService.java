package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.BloodSampleResponse;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.BloodSampleRepository;
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
public class BloodSampleService {

    private final BloodSampleRepository bloodSampleRepository;
    private final ActivityRepository activityRepository;
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

    public ApiDataResponseBuilder getAllBloodSamples(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<BloodSampleResponse> result = bloodSampleRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data blood samples")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getBloodSampleById(String id) {
        BloodSample bs = bloodSampleRepository.findByBloodSampleIdAndDeletedAtIsNull(id).orElse(null);
        if (bs == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data blood sample tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Berhasil mendapatkan data blood sample")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addBloodSample(BloodSampleRequest request) {
        Activity activity = activityRepository.findById(request.getActivityId()).orElseThrow(() -> new DataNotFoundException("Activity tidak ditemukan"));

        BloodSample bs = new BloodSample();
        bs.setBloodSampleId(buildBloodSampleId(nextSequence()));
        bs.setActivity(activity);
        bs.setSampleCode(deriveSampleCode(activity.getActivityId()));
        bs.setCollectedBy(request.getCollectedBy());
        try {
            if (request.getSampleTime() != null) bs.setSampleTime(LocalDateTime.parse(request.getSampleTime()));
        } catch (DateTimeParseException ignored) {
        }
        bs.setSampleType(request.getSampleType());
        bs.setTubeType(request.getTubeType());
        bs.setVolumeMl(request.getVolumeMl());
        Integer uid = getCurrentUserId();
        bs.setCreatedBy(uid);
        bs.setUpdatedBy(uid);
        bs.setStatus(EntityStatus.ACTIVE);

        bloodSampleRepository.save(bs);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Blood sample berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateBloodSample(String id, BloodSampleUpdateRequest request) {
        Optional<BloodSample> opt = bloodSampleRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data blood sample tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        BloodSample bs = opt.get();
        Integer uid = getCurrentUserId();
        bs.setSampleCode(deriveSampleCode(bs.getActivity() == null ? null : bs.getActivity().getActivityId()));
        if (request.getCollectedBy() != null) bs.setCollectedBy(request.getCollectedBy());
        try {
            if (request.getSampleTime() != null) bs.setSampleTime(LocalDateTime.parse(request.getSampleTime()));
        } catch (DateTimeParseException ignored) {
        }
        if (request.getSampleType() != null) bs.setSampleType(request.getSampleType());
        if (request.getTubeType() != null) bs.setTubeType(request.getTubeType());
        if (request.getVolumeMl() != null) bs.setVolumeMl(request.getVolumeMl());

        bs.setUpdatedBy(uid);
        bloodSampleRepository.save(bs);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Blood sample berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteBloodSample(String id) {
        Optional<BloodSample> opt = bloodSampleRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data blood sample tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        BloodSample bs = opt.get();
        Integer uid = getCurrentUserId();
        bs.setDeletedAt(LocalDateTime.now());
        bs.setDeletedBy(uid);
        bs.setStatus(EntityStatus.DELETED);
        bs.setUpdatedBy(uid);
        bloodSampleRepository.save(bs);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Blood sample berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateBloodSampleStatus(String id, BloodSampleStatusUpdateRequest request) {
        Optional<BloodSample> opt = bloodSampleRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data blood sample tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status blood sample tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        BloodSample bs = opt.get();
        Integer uid = getCurrentUserId();
        bs.setStatus(request.getStatus());
        bs.setUpdatedBy(uid);
        bs.setUpdatedAt(LocalDateTime.now());
        bloodSampleRepository.save(bs);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Status blood sample berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchBloodSamples(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<BloodSampleResponse> result;
        if (keyword == null || keyword.isBlank()) {
            result = bloodSampleRepository.findAllActive(pageable).map(this::mapToResponse);
        } else {
            result = bloodSampleRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);
        }

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari blood samples")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private BloodSampleResponse mapToResponse(BloodSample bs) {
        BloodSampleResponse resp = modelMapper.map(bs, BloodSampleResponse.class);
        resp.setStatus(bs.getStatus() == null ? null : bs.getStatus().name());
        resp.setActivityId(bs.getActivity() == null ? null : bs.getActivity().getActivityId());
        return resp;
    }

    private int nextSequence() {
        try {
            Optional<BloodSample> lastOpt = bloodSampleRepository.findTopByDeletedAtIsNullOrderByBloodSampleIdDesc();
            if (lastOpt.isPresent()) {
                String lastId = lastOpt.get().getBloodSampleId();
                if (lastId != null && lastId.startsWith("BS-")) {
                    String num = lastId.substring(3).replaceAll("[^0-9]", "");
                    if (!num.isBlank()) return Integer.parseInt(num) + 1;
                }
            }
        } catch (Exception ignored) {
        }
        return 1;
    }

    private String buildBloodSampleId(int seq) {
        return String.format("BS-%03d", seq);
    }

    private String deriveSampleCode(String activityId) {
        if (activityId == null || activityId.isBlank()) {
            return null;
        }

        String[] parts = activityId.split("-");
        if (parts.length < 4) {
            return activityId;
        }

        StringBuilder sampleCode = new StringBuilder();
        for (int i = 2; i < parts.length - 1; i++) {
            if (sampleCode.length() > 0) {
                sampleCode.append("-");
            }
            sampleCode.append(parts[i]);
        }

        return sampleCode.length() == 0 ? activityId : sampleCode.toString();
    }
}
