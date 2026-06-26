package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.InfusionMonitoringResponse;
import com.tujuhsembilan.glucoseclamp.model.GlobalConfiguration;
import com.tujuhsembilan.glucoseclamp.model.InfusionMonitoring;
import com.tujuhsembilan.glucoseclamp.model.LabResult;
import com.tujuhsembilan.glucoseclamp.model.Protocol;
import com.tujuhsembilan.glucoseclamp.model.Session;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.GlobalConfigurationRepository;
import com.tujuhsembilan.glucoseclamp.repository.InfusionMonitoringRepository;
import com.tujuhsembilan.glucoseclamp.repository.LabResultRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfusionMonitoringService {

    private final InfusionMonitoringRepository infusionMonitoringRepository;
    private final SessionRepository sessionRepository;
    private final LabResultRepository labResultRepository;
    private final ModelMapper modelMapper;
    private final GlobalConfigurationRepository globalConfigurationRepository;

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

    public ApiDataResponseBuilder getAllInfusionMonitorings(int pageNumber, int pageSize, Boolean includeSystemGenerated) {
        boolean includeSystem = (includeSystemGenerated != null) && includeSystemGenerated;

        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        
        Page<InfusionMonitoringResponse> result = infusionMonitoringRepository
                .findAllActive(includeSystem, pageable)
                .map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data infusion monitorings")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getInfusionMonitoringsBySessionId(Long sessionId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<InfusionMonitoring> pageResult = infusionMonitoringRepository.findBySessionId(sessionId, pageable);

        List<InfusionMonitoringResponse> responseList = pageResult.getContent().stream()
                .map(monitoring -> modelMapper.map(monitoring, InfusionMonitoringResponse.class))
                .toList();

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .message("Success fetch infusion monitoring by session ID")
                .data(responseList)
                .build();
    }

    public ApiDataResponseBuilder getInfusionMonitoringById(Long id) {
        Optional<InfusionMonitoring> opt = infusionMonitoringRepository.findByIdWithSessionAndProtocol(id);
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
        
        LocalDateTime parsedTime = null;
        try {
            if (request.getTime() != null) {
                parsedTime = LocalDateTime.parse(request.getTime());
            }
        } catch (DateTimeParseException ignored) {
            return ApiDataResponseBuilder.builder()
                    .message("Format waktu tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        if (parsedTime != null) {
            boolean isDuplicate = infusionMonitoringRepository
                    .existsBySessionAndTimeAndStatusAndDeletedAtIsNull(session, parsedTime, EntityStatus.ACTIVE);
            
            if (isDuplicate) {
                return ApiDataResponseBuilder.builder()
                        .message("Data monitoring untuk sesi pada waktu tersebut sudah terekam")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .status(HttpStatus.BAD_REQUEST)
                        .build();
            }
        }

        InfusionMonitoring im = new InfusionMonitoring();
        im.setSession(session);
        try {
            if (request.getTime() != null) im.setTime(LocalDateTime.parse(request.getTime()));
        } catch (DateTimeParseException ignored) {
        }
        
        im.setGlucoseValue(request.getGlucoseValue());
        
        BigDecimal calculatedGir = calculateGir(session, request.getGlucoseValue());
        im.setRecommendedGir(request.getRateMinKg());

        im.setActualGir(request.getConfirmationRateMinKg());
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
    public ApiDataResponseBuilder updateInfusionMonitoring(Long id, InfusionMonitoringUpdateRequest request) {
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
            BigDecimal calculatedGir = calculateGir(im.getSession(), request.getGlucoseValue());
            im.setRecommendedGir(calculatedGir);
        }
        
        if (request.getConfirmationRateMinKg() != null) im.setActualGir(request.getConfirmationRateMinKg());
        if (request.getRateMinKg() != null) im.setRecommendedGir(request.getRateMinKg());
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
    public ApiDataResponseBuilder deleteInfusionMonitoring(Long id) {
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
    public ApiDataResponseBuilder updateInfusionMonitoringStatus(Long id, InfusionMonitoringStatusUpdateRequest request) {
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

    public ApiDataResponseBuilder searchInfusionMonitorings(String keyword, int pageNumber, int pageSize, Boolean includeSystemGenerated) {
        boolean includeSystem = (includeSystemGenerated != null) && includeSystemGenerated;
        
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<InfusionMonitoringResponse> result;

        if (keyword == null || keyword.isBlank()) {
            result = infusionMonitoringRepository.findAllActive(includeSystem, pageable).map(this::mapToResponse);
        } else {
            result = infusionMonitoringRepository.searchByKeyword(keyword.trim(), includeSystem, pageable).map(this::mapToResponse);
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

        InfusionMonitoring draft = lastActiveOpt.get();
        BigDecimal latestGlucose = draft.getGlucoseValue();
        
        BigDecimal recommendedGir = calculateGir(session, latestGlucose);

        return ApiDataResponseBuilder.builder()
                .data(Map.of(
                    "infusionId", draft.getInfusionId(),
                    "sessionId", sessionId,
                    "latestGlucoseValue", latestGlucose,
                    "recommendedGir", recommendedGir
                ))
                .message("Rekomendasi GIR berhasil diambil berdasarkan data aktual terakhir")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public BigDecimal calculateActualGb(Session session) {
        if (session == null) {
            return new BigDecimal("95"); 
        }

        List<LabResult> baselineResults = labResultRepository.findBaselineGlucoseBySessionId(session.getSessionId());
        if (baselineResults.isEmpty()) {
            return new BigDecimal("95"); 
        }

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;
        for (LabResult lr : baselineResults) {
            if (lr.getValue() != null) {
                sum = sum.add(lr.getValue());
                count++;
            }
        }

        return count > 0 
                ? sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) 
                : new BigDecimal("95");
    }

    /**
     * Logika Penentuan Fase GIR (Sesuai Simulasi Data Excel - Pendekatan 1: Sequential Hybrid)
     */
    public BigDecimal calculateGir(Session session, BigDecimal currentGlucose) {
        if (currentGlucose == null || currentGlucose.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal gb = calculateActualGb(session);

        // Ambil semua riwayat monitoring aktif, urut descending (terbaru di indeks 0)
        List<InfusionMonitoring> activeMonitorings = infusionMonitoringRepository
                .findBySessionAndStatusAndDeletedAtIsNull(session, EntityStatus.ACTIVE)
                .stream()
                .sorted((im1, im2) -> im2.getInfusionId().compareTo(im1.getInfusionId()))
                .toList();

        // Hitung jumlah langkah infus yang sudah berjalan (rate > 0)
        long activeInfusionCount = activeMonitorings.stream()
                .filter(im -> {
                    BigDecimal effectiveRate = im.getActualGir() != null 
                            ? im.getActualGir() 
                            : im.getRecommendedGir();
                    return effectiveRate != null && effectiveRate.compareTo(BigDecimal.ZERO) > 0;
                })
                .count();

        // Fase 0: Belum ada infus aktif yang berjalan
        if (activeInfusionCount == 0) {
            Protocol protocol = session != null ? session.getProtocol() : null;
            
            // Membaca persentase trigger drop secara dinamis dari Protocol (default fallback: 10.00%)
            BigDecimal dropPercent = (protocol != null && protocol.getGlucoseDropTriggerPercentage() != null)
                    ? protocol.getGlucoseDropTriggerPercentage()
                    : new BigDecimal("10.00");

            // Membaca initial infusion rate secara dinamis dari Protocol (default fallback: 2.00)
            BigDecimal initialRate = (protocol != null && protocol.getInitialGlucoseInfusionRate() != null)
            ? protocol.getInitialGlucoseInfusionRate()
            : new BigDecimal("2.00");
            
            // Ubah menjadi desimal (misal: 10.00% / 100 = 0.10)
            BigDecimal dropDecimal = dropPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP); 
            // Kurangi dari 1 untuk mendapatkan faktor sisa (1 - 0.10 = 0.90)
            BigDecimal initialDrop = BigDecimal.ONE.subtract(dropDecimal);
            log.info("Initial Drop Threshold Factor: " + initialDrop);
            BigDecimal threshold = gb.multiply(initialDrop);
            // // Cek apakah glukosa turun >= threshold dari Gb
            if (currentGlucose.compareTo(threshold) <= 0) {
                return initialRate; // Mulai Fase 1 (Dosis Awal)
            } else {
                return BigDecimal.ZERO; // Tetap Fase 0
            }
            // return BigDecimal.ZERO;
        }

        BigDecimal girCurrent = activeMonitorings.stream()
                    .map(InfusionMonitoring::getActualGir)
                    .filter(rate -> rate != null && rate.compareTo(BigDecimal.ZERO) > 0)
                    .findFirst()
                    .orElseGet(() -> {
                        // Fallback jika belum pernah ada konfirmasi sama sekali, ambil recommended terdekat
                        return activeMonitorings.stream()
                                .map(InfusionMonitoring::getRecommendedGir)
                                .filter(rate -> rate != null && rate.compareTo(BigDecimal.ZERO) > 0)
                                .findFirst()
                                .orElse(null);
                    });


        BigDecimal fmi = gb.divide(currentGlucose, 4, RoundingMode.HALF_UP);

        return girCurrent.multiply(fmi).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateFlowRate(BigDecimal gir, BigDecimal weight) {
        if (gir == null || gir.compareTo(BigDecimal.ZERO) <= 0 || weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal factor = new BigDecimal("0.6");
        return gir.multiply(weight).multiply(factor).setScale(1, RoundingMode.HALF_UP);
    }

    private InfusionMonitoringResponse mapToResponse(InfusionMonitoring im) {
        InfusionMonitoringResponse resp = modelMapper.map(im, InfusionMonitoringResponse.class);
        resp.setStatus(im.getStatus() == null ? null : im.getStatus().name());
        resp.setSessionId(im.getSession() == null ? null : im.getSession().getSessionId());
        return resp;
    }

    // private String nextInfusionId() {
    //     Optional<InfusionMonitoring> lastOpt = infusionMonitoringRepository.findTopByDeletedAtIsNullOrderByInfusionIdDesc();
    //     if (lastOpt.isEmpty() || lastOpt.get().getInfusionId() == null || lastOpt.get().getInfusionId().isBlank()) {
    //         return "INF-001";
    //     }

    //     String lastId = lastOpt.get().getInfusionId().trim();
    //     if (!lastId.startsWith("INF-")) {
    //         return "INF-001";
    //     }

    //     try {
    //         int sequence = Integer.parseInt(lastId.substring(4));
    //         return String.format("INF-%03d", sequence + 1);
    //     } catch (NumberFormatException ex) {
    //         return "INF-001";
    //     }
    // }
}