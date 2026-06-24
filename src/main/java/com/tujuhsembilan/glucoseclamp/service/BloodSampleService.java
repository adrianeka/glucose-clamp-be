package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.BloodSampleResponse;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
import com.tujuhsembilan.glucoseclamp.model.Activity;
import com.tujuhsembilan.glucoseclamp.model.BloodSample;
import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.LabResult;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.ActivityRepository;
import com.tujuhsembilan.glucoseclamp.repository.BloodSampleRepository;
import com.tujuhsembilan.glucoseclamp.repository.InfusionMonitoringRepository;
import com.tujuhsembilan.glucoseclamp.repository.LabResultRepository;

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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class BloodSampleService {

    private final BloodSampleRepository bloodSampleRepository;
    private final ActivityRepository activityRepository;
    private final LabResultRepository labResultRepository;
    private final InfusionMonitoringRepository infusionMonitoringRepository;
    private final ModelMapper modelMapper;
    private final ActivityService activityService;
    private final SseService sseService;
    private final InfusionMonitoringService infusionMonitoringService;

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

    private boolean isPkCOrCpeptide(String sampleType) {
        if (sampleType == null) return false;
        String trimmed = sampleType.trim();
        return "C-Peptide".equalsIgnoreCase(trimmed) 
            || "PK-C".equalsIgnoreCase(trimmed) 
            || "PK".equalsIgnoreCase(trimmed);
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
        // 1. Validasi tipe sampel C-Peptide / PK-C
        if (isPkCOrCpeptide(request.getSampleType())) {
            if (request.getLabResults() == null || request.getLabResults().size() < 2) {
                return ApiDataResponseBuilder.builder()
                        .message("Untuk tipe sampel C-Peptide / PK-C, wajib menyertakan minimal 2 lab result")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }

        // 2. Cari Activity, Session, dan Protocol
        Activity activity = activityRepository.findById(request.getActivityId())
                .orElseThrow(() -> new DataNotFoundException("Activity tidak ditemukan"));
        
        Session session = activity.getSession();
        Protocol protocol = (session != null) ? session.getProtocol() : null;
        
        Integer uid = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        // 3. Simpan Blood Sample
        BloodSample bs = new BloodSample();
        String bsId = buildBloodSampleId(nextSequence());
        bs.setBloodSampleId(bsId);
        bs.setActivity(activity);
        bs.setSampleCode(deriveSampleCode(activity.getActivityId()));
        bs.setCollectedBy(request.getCollectedBy());
        if (request.getSampleTime() != null) {
            try {
                bs.setSampleTime(LocalDateTime.parse(request.getSampleTime()));
            } catch (Exception ignored) {}
        }
        bs.setSampleType(request.getSampleType());
        bs.setTubeType(request.getTubeType());
        bs.setVolumeMl(request.getVolumeMl());
        bs.setCreatedBy(uid);
        bs.setUpdatedBy(uid);
        bs.setStatus(EntityStatus.ACTIVE);
        bloodSampleRepository.save(bs);

        // 4. Simpan Lab Results dengan penentuan rentang referensi dan abnormal flag otomatis
        int index = 1;
        java.math.BigDecimal glucoseValueForInfusion = null;

        for (BloodSampleRequest.LabResultDetails lrDetail : request.getLabResults()) {
            java.math.BigDecimal refMin = null;
            java.math.BigDecimal refMax = null;
            String calculatedFlag = "NORMAL";

            // Jika parameter adalah Glucose, ambil data batas dari Protocol
            if (protocol != null && "Glucose".equalsIgnoreCase(lrDetail.getParameterName())) {
                refMin = protocol.getGlucoseTargetMin();
                refMax = protocol.getGlucoseTargetMax();
                java.math.BigDecimal val = lrDetail.getValue();

                if (val != null) {
                    java.math.BigDecimal minExtreme = protocol.getGlucoseTargetMinExtreme();
                    java.math.BigDecimal maxExtreme = protocol.getGlucoseTargetMaxExtreme();

                    // Logika penentuan Abnormal Flag berdasarkan batas Protocol
                    if (minExtreme != null && val.compareTo(minExtreme) < 0) {
                        calculatedFlag = "EXTREME_LOW";
                    } else if (refMin != null && val.compareTo(refMin) < 0) {
                        calculatedFlag = "LOW";
                    } else if (maxExtreme != null && val.compareTo(maxExtreme) > 0) {
                        calculatedFlag = "EXTREME_HIGH";
                    } else if (refMax != null && val.compareTo(refMax) > 0) {
                        calculatedFlag = "HIGH";
                    } else {
                        calculatedFlag = "NORMAL";
                    }
                }
            } else {
                calculatedFlag = null; 
            }

            LabResult lr = LabResult.builder()
                    .labResultId(String.format("LR-%s-%d", bsId, index++))
                    .bloodSample(bs)
                    .parameterName(lrDetail.getParameterName())
                    .value(lrDetail.getValue())
                    .referenceRangeMin(refMin) // Otomatis terisi dari Protocol
                    .referenceRangeMax(refMax) // Otomatis terisi dari Protocol
                    .unit(lrDetail.getUnit() != null ? lrDetail.getUnit() : (protocol != null && "Glucose".equalsIgnoreCase(lrDetail.getParameterName()) ? protocol.getGlucoseTargetUnit() : null))
                    .abnormalFlag(calculatedFlag) // Otomatis terhitung di BE
                    .build();
            
            lr.setCreatedBy(uid);
            lr.setUpdatedBy(uid);
            lr.setStatus(EntityStatus.ACTIVE);
            labResultRepository.save(lr);

            if ("Glucose".equalsIgnoreCase(lrDetail.getParameterName()) || glucoseValueForInfusion == null) {
                glucoseValueForInfusion = lrDetail.getValue();
            }
        }

        // 5. Simpan ke Infusion Monitoring (Hanya jika BUKAN tipe PK-C / C-Peptide)
        if (session != null && !isPkCOrCpeptide(request.getSampleType())) {
            InfusionMonitoring im = new InfusionMonitoring();
            im.setInfusionId("INF-" + bsId);
            im.setSession(session);
            im.setTime(now);
            im.setGlucoseValue(glucoseValueForInfusion);

            BigDecimal calculatedGir = infusionMonitoringService.calculateGir(session, glucoseValueForInfusion);
            im.setRateMinKg(calculatedGir);
            im.setMonitoredBy(uid);
            im.setStatus(EntityStatus.ACTIVE);
            im.setCreatedBy(uid);
            im.setUpdatedBy(uid);
            infusionMonitoringRepository.save(im);
        }

        try {
            activityService.completeActivity(activity.getActivityId());
        } catch (Exception e) {
            log.error("Gagal mengubah status aktivitas {} menjadi complete: {}", activity.getActivityId(), e.getMessage());
            throw e; 
        }

        // 7. Mengirimkan Event Real-Time melalui SSE
        if (session != null && session.getSessionId() != null) {
            try {
                Map<String, Object> ssePayload = Map.of(
                    "activityId", activity.getActivityId(),
                    "activityName", activity.getActivityType() != null ? activity.getActivityType() : ""
                );
                sseService.sendEvent(session.getSessionId(), "BLOOD_DRAW_ADDED", ssePayload);
            } catch (Exception e) {
                log.warn("Gagal mengirimkan event SSE untuk aktivitas {}: {}", activity.getActivityId(), e.getMessage());
            }
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Blood sample berhasil disimpan.")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateBloodSample(String id, BloodSampleRequest request) {
        // 1. Cari Blood Sample yang akan di-update
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
        LocalDateTime now = LocalDateTime.now();

        // 2. Validasi khusus C-Peptide / PK-C
        if (isPkCOrCpeptide(request.getSampleType())) {
            if (request.getLabResults() == null || request.getLabResults().size() < 2) {
                return ApiDataResponseBuilder.builder()
                        .message("Untuk tipe sampel C-Peptide / PK-C, wajib menyertakan minimal 2 lab result")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }

        // 3. Update data utama Blood Sample
        bs.setSampleCode(deriveSampleCode(bs.getActivity() == null ? null : bs.getActivity().getActivityId()));
        if (request.getCollectedBy() != null) bs.setCollectedBy(request.getCollectedBy());
        if (request.getSampleTime() != null) {
            try {
                bs.setSampleTime(LocalDateTime.parse(request.getSampleTime()));
            } catch (Exception ignored) {}
        }
        if (request.getSampleType() != null) bs.setSampleType(request.getSampleType());
        if (request.getTubeType() != null) bs.setTubeType(request.getTubeType());
        if (request.getVolumeMl() != null) bs.setVolumeMl(request.getVolumeMl());
        bs.setUpdatedBy(uid);
        bloodSampleRepository.save(bs);

        // 4. Sinkronisasi Lab Results (Sequential Sync)
        List<LabResult> existingLrs = labResultRepository.findByBloodSampleAndDeletedAtIsNull(bs); 
        List<BloodSampleRequest.LabResultDetails> newLrs = request.getLabResults() != null ? request.getLabResults() : new java.util.ArrayList<>();

        java.math.BigDecimal glucoseValueForInfusion = null;
        Protocol protocol = (bs.getActivity() != null && bs.getActivity().getSession() != null) 
                ? bs.getActivity().getSession().getProtocol() : null;

        int maxCount = Math.max(existingLrs.size(), newLrs.size());
        for (int i = 0; i < maxCount; i++) {
            if (i < newLrs.size()) {
                BloodSampleRequest.LabResultDetails lrDetail = newLrs.get(i);
                java.math.BigDecimal refMin = null;
                java.math.BigDecimal refMax = null;
                String calculatedFlag = "NORMAL";

                if (protocol != null && "Glucose".equalsIgnoreCase(lrDetail.getParameterName())) {
                    refMin = protocol.getGlucoseTargetMin();
                    refMax = protocol.getGlucoseTargetMax();
                    java.math.BigDecimal val = lrDetail.getValue();

                    if (val != null) {
                        java.math.BigDecimal minExtreme = protocol.getGlucoseTargetMinExtreme();
                        java.math.BigDecimal maxExtreme = protocol.getGlucoseTargetMaxExtreme();

                        if (minExtreme != null && val.compareTo(minExtreme) < 0) {
                            calculatedFlag = "EXTREME_LOW";
                        } else if (refMin != null && val.compareTo(refMin) < 0) {
                            calculatedFlag = "LOW";
                        } else if (maxExtreme != null && val.compareTo(maxExtreme) > 0) {
                            calculatedFlag = "EXTREME_HIGH";
                        } else if (refMax != null && val.compareTo(refMax) > 0) {
                            calculatedFlag = "HIGH";
                        } else {
                            calculatedFlag = "NORMAL";
                        }
                    }
                } else {
                    calculatedFlag = null;
                }

                if (i < existingLrs.size()) {
                    LabResult existingLr = existingLrs.get(i);
                    existingLr.setParameterName(lrDetail.getParameterName());
                    existingLr.setValue(lrDetail.getValue());
                    existingLr.setReferenceRangeMin(refMin);
                    existingLr.setReferenceRangeMax(refMax);
                    existingLr.setUnit(lrDetail.getUnit() != null ? lrDetail.getUnit() : (protocol != null && "Glucose".equalsIgnoreCase(lrDetail.getParameterName()) ? protocol.getGlucoseTargetUnit() : null));
                    existingLr.setAbnormalFlag(calculatedFlag);
                    existingLr.setUpdatedBy(uid);
                    existingLr.setUpdatedAt(now);
                    labResultRepository.save(existingLr);
                } else {
                    LabResult newLr = LabResult.builder()
                            .labResultId(String.format("LR-%s-%d", bs.getBloodSampleId(), i + 1))
                            .bloodSample(bs)
                            .parameterName(lrDetail.getParameterName())
                            .value(lrDetail.getValue())
                            .referenceRangeMin(refMin)
                            .referenceRangeMax(refMax)
                            .unit(lrDetail.getUnit() != null ? lrDetail.getUnit() : (protocol != null && "Glucose".equalsIgnoreCase(lrDetail.getParameterName()) ? protocol.getGlucoseTargetUnit() : null))
                            .abnormalFlag(calculatedFlag)
                            .build();
                    newLr.setCreatedBy(uid);
                    newLr.setUpdatedBy(uid);
                    newLr.setCreatedAt(now);
                    newLr.setUpdatedAt(now);
                    newLr.setStatus(EntityStatus.ACTIVE);
                    labResultRepository.save(newLr);
                }

                if ("Glucose".equalsIgnoreCase(lrDetail.getParameterName()) || glucoseValueForInfusion == null) {
                    glucoseValueForInfusion = lrDetail.getValue();
                }
            } else {
                LabResult lrToDelete = existingLrs.get(i);
                lrToDelete.setDeletedAt(now);
                lrToDelete.setDeletedBy(uid);
                lrToDelete.setStatus(EntityStatus.DELETED);
                labResultRepository.save(lrToDelete);
            }
        }

        // 5. Update data Infusion Monitoring yang terkait (Hanya jika BUKAN tipe PK-C / C-Peptide)
        if (!isPkCOrCpeptide(request.getSampleType())) {
            Optional<InfusionMonitoring> imOpt = infusionMonitoringRepository.findById("INF-" + bs.getBloodSampleId());
            if (imOpt.isPresent()) {
                InfusionMonitoring im = imOpt.get();
                im.setGlucoseValue(glucoseValueForInfusion);
                im.setUpdatedBy(uid);
                im.setUpdatedAt(now);
                infusionMonitoringRepository.save(im);
            }
        }

        // 6. Jaring pengaman status COMPLETE
        if (bs.getActivity() != null) {
            try {
                activityService.completeActivity(bs.getActivity().getActivityId());
            } catch (Exception e) {
                log.error("Gagal menyinkronkan status aktivitas {} saat update blood sample: {}", 
                    bs.getActivity().getActivityId(), e.getMessage());
            }
        }

        // 7. Event Real-Time SSE
        if (bs.getActivity() != null && bs.getActivity().getSession() != null) {
            Session session = bs.getActivity().getSession();
            if (session.getSessionId() != null) {
                try {
                    Map<String, Object> ssePayload = Map.of(
                        "activityId", bs.getActivity().getActivityId(),
                        "activityName", bs.getActivity().getActivityType() != null ? bs.getActivity().getActivityType() : ""
                    );
                    sseService.sendEvent(session.getSessionId(), "BLOOD_DRAW_UPDATED", ssePayload);
                } catch (Exception e) {
                    log.warn("Gagal mengirimkan event SSE update untuk aktivitas {}: {}", 
                        bs.getActivity().getActivityId(), e.getMessage());
                }
            }
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(bs))
                .message("Blood sample beserta data Lab Result dan Infusion terkait berhasil diupdate")
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
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return 1;
    }

    private String buildBloodSampleId(int seq) {
        return String.format("BS-%03d", seq);
    }

    private String deriveSampleCode(Long activityId) {
        return activityId == null
                ? null
                : "SA-" + activityId;
    }
}