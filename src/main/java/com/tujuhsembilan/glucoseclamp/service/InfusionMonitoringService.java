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
import lombok.extern.slf4j.Slf4j;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
        
        // 1. Hitung nilai GIR otomatis menggunakan formula khusus
        BigDecimal calculatedGir = calculateGir(session, request.getGlucoseValue());
        im.setRateMinKg(calculatedGir);

        im.setConfirmationRateMinKg(request.getConfirmationRateMinKg());
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
        
        if (request.getGlucoseValue() != null) {
            im.setGlucoseValue(request.getGlucoseValue());
            // Hitung ulang GIR jika kadar glukosa diubah
            BigDecimal calculatedGir = calculateGir(im.getSession(), request.getGlucoseValue());
            im.setRateMinKg(calculatedGir);
        }
        
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

    public ApiDataResponseBuilder getLatestGirRecommendation(Long sessionId) {
        Session session = sessionRepository.findByIdAndDeletedAtIsNull(sessionId).orElse(null);
        if (session == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Session tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Optional<InfusionMonitoring> lastActiveOpt = infusionMonitoringRepository
                .findTopBySessionAndStatusAndDeletedAtIsNullOrderByTimeDesc(session, EntityStatus.ACTIVE);

        if (lastActiveOpt.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Belum ada data glukosa darah terekam untuk sesi ini.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        BigDecimal latestGlucose = lastActiveOpt.get().getGlucoseValue();
        
        // PERBAIKAN: Ambil nilai GIR yang sudah dihitung dan disimpan sebelumnya
        BigDecimal recommendedGir = lastActiveOpt.get().getRateMinKg();
        
        // Jika karena suatu hal nilainya kosong di DB, baru lakukan kalkulasi ulang sebagai pengaman
        if (recommendedGir == null) {
            recommendedGir = calculateGir(session, latestGlucose);
        }

        return ApiDataResponseBuilder.builder()
                .data(Map.of(
                    "sessionId", sessionId,
                    "latestGlucoseValue", latestGlucose,
                    "recommendedGir", recommendedGir
                ))
                .message("Rekomendasi GIR berhasil diambil dari data glukosa terakhir")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    /**
     * Logika Perhitungan GIR Matematis & Fallback Berdasarkan Gambar Rekomendasi
     */
    public BigDecimal calculateGir(Session session, BigDecimal currentGlucose) {
        if (currentGlucose == null || currentGlucose.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // A. Cari Nilai GIR Sebelumnya (gir_current) dari monitoring aktif terakhir pada sesi ini
        BigDecimal girCurrent = null;
        if (session != null) {
            Optional<InfusionMonitoring> lastActiveOpt = infusionMonitoringRepository.findTopBySessionAndStatusAndDeletedAtIsNullOrderByTimeDesc(session, EntityStatus.ACTIVE);
            if (lastActiveOpt.isPresent()) {
                girCurrent = lastActiveOpt.get().getRateMinKg();
            }
        }

        // B. FALLBACK JIKA GIR SEBELUMNYA KOSONG (Menggunakan aturan berjenjang pada gambar Anda)
        if (girCurrent == null || girCurrent.compareTo(BigDecimal.ZERO) == 0) {
            double glc = currentGlucose.doubleValue();
            if (glc > 250) {
                return new BigDecimal("2.00");
            } else if (glc >= 180) {
                return new BigDecimal("3.00");
            } else if (glc >= 100) {
                return new BigDecimal("4.00");
            } else {
                return new BigDecimal("5.00");
            }
        }

        // C. Tentukan Target Glucose (Default ke 120, atau rata-rata target Protocol)
        BigDecimal targetGlucose = new BigDecimal("120");
        if (session != null && session.getProtocol() != null) {
            BigDecimal min = session.getProtocol().getGlucoseTargetMin();
            BigDecimal max = session.getProtocol().getGlucoseTargetMax();
            if (min != null && max != null) {
                targetGlucose = min.add(max).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            } else if (min != null) {
                targetGlucose = min;
            } else if (max != null) {
                targetGlucose = max;
            }
        }

        // D. fmi = target_glucose / current_glucose
        BigDecimal fmi = targetGlucose.divide(currentGlucose, 4, RoundingMode.HALF_UP);

        // E. gir_new = gir_current * fmi
        BigDecimal girNew = girCurrent.multiply(fmi).setScale(2, RoundingMode.HALF_UP);

        // F. Safety Limit (Pembatasan Perubahan Maksimal 25%)
        BigDecimal maxChange = new BigDecimal("0.25");
        BigDecimal upper = girCurrent.multiply(BigDecimal.ONE.add(maxChange)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lower = girCurrent.multiply(BigDecimal.ONE.subtract(maxChange)).setScale(2, RoundingMode.HALF_UP);

        if (girNew.compareTo(upper) > 0) {
            girNew = upper;
        } else if (girNew.compareTo(lower) < 0) {
            girNew = lower;
        }

        return girNew;
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