package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.BulkUpdateSamplingScheduleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BulkUpdateSamplingScheduleRequest.UpdateItem;
import com.tujuhsembilan.glucoseclamp.dto.request.SamplingScheduleRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.SamplingScheduleResponse;
import com.tujuhsembilan.glucoseclamp.exception.classes.DataNotFoundException;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SamplingSchedulesService {
    @PersistenceContext
    private EntityManager entityManager; 

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

    // @Transactional
    // public ApiDataResponseBuilder addSamplingSchedule(SamplingScheduleRequest request) {

    //     Optional<Protocol> protocolOpt =
    //             protocolRepository.findById(request.getProtocolId());

    //     if (protocolOpt.isEmpty()
    //             || EntityStatus.DELETED.equals(protocolOpt.get().getStatus())) {
    //         return ApiDataResponseBuilder.builder()
    //                 .message("Protocol tidak ditemukan")
    //                 .statusCode(HttpStatus.BAD_REQUEST.value())
    //                 .status(HttpStatus.BAD_REQUEST)
    //                 .build();
    //     }

    //     if (request.getPhaseDuration() < request.getTimeInterval()) {
    //         return ApiDataResponseBuilder.builder()
    //                 .message("Phase duration tidak boleh lebih kecil dari interval")
    //                 .statusCode(HttpStatus.BAD_REQUEST.value())
    //                 .status(HttpStatus.BAD_REQUEST)
    //                 .build();
    //     }

    //     if (request.getPhaseDuration() % request.getTimeInterval() != 0) {
    //         return ApiDataResponseBuilder.builder()
    //                 .message("Phase duration harus habis dibagi interval")
    //                 .statusCode(HttpStatus.BAD_REQUEST.value())
    //                 .status(HttpStatus.BAD_REQUEST)
    //                 .build();
    //     }

    //     int totalSchedule =
    //             request.getPhaseDuration() / request.getTimeInterval();

    //     String prefix = request.getLabelPrefix();

    //     int nextSequence = 1;

    //     Optional<SamplingSchedule> lastCode =
    //             samplingScheduleRepository
    //                     .findTopByProtocolProtocolIdAndScheduleCodeStartingWithAndStatusOrderBySamplingScheduleIdDesc(
    //                             request.getProtocolId(),
    //                             prefix,
    //                             EntityStatus.ACTIVE
    //                     );

    //     if (lastCode.isPresent()) {
    //         String code = lastCode.get().getScheduleCode();

    //         nextSequence =
    //                 Integer.parseInt(code.replace(prefix + "-", "")) + 1;
    //     }

    //     SamplingSchedule lastSchedule =
    //             samplingScheduleRepository
    //                     .findTopByProtocolProtocolIdAndStatusOrderByRelativeMinuteDesc(
    //                             request.getProtocolId(),
    //                             EntityStatus.ACTIVE
    //                     )
    //                     .orElse(null);

    //     int currentMinute =
    //             lastSchedule == null
    //                     ? 0
    //                     : lastSchedule.getRelativeMinute();

    //     Integer currentUserId = getCurrentUserId();
    //     LocalDateTime now = LocalDateTime.now();

    //     List<SamplingSchedule> createdSchedules = new ArrayList<>();

    //     for (int i = 0; i < totalSchedule; i++) {

    //         currentMinute += request.getTimeInterval();

    //         SamplingSchedule detail =
    //                 SamplingSchedule.builder()
    //                         .protocol(protocolOpt.get())
    //                         .phaseCode(request.getPhaseCode())
    //                         .timeInterval(request.getTimeInterval())
    //                         .relativeMinute(currentMinute)
    //                         .scheduleCode(prefix + "-" + nextSequence++)
    //                         .bloodRaw(request.getBloodRaw())
    //                         .insulinInject(request.getInsulinInject())
    //                         .pkSampleCollection(request.getPkSampleCollection())
    //                         .build();

    //         detail.setCreatedAt(now);
    //         detail.setUpdatedAt(now);
    //         detail.setCreatedBy(currentUserId);
    //         detail.setUpdatedBy(currentUserId);
    //         detail.setStatus(EntityStatus.ACTIVE);

    //         createdSchedules.add(detail);
    //     }

    //     samplingScheduleRepository.saveAll(createdSchedules);

    //     return ApiDataResponseBuilder.builder()
    //             .data(createdSchedules.stream()
    //                     .map(this::mapToResponse)
    //                     .toList())
    //             .message("Sampling schedule berhasil ditambahkan")
    //             .statusCode(HttpStatus.CREATED.value())
    //             .status(HttpStatus.CREATED)
    //             .build();
    // }

    @Transactional
    public ApiDataResponseBuilder addSamplingSchedule(SamplingScheduleRequest request) {
        // 1. Validasi Protocol
        Protocol protocol = protocolRepository
            .findByIdAndDeletedAtIsNull(request.getProtocolId())
            .orElseThrow(() -> new RuntimeException("Protocol tidak ditemukan"));

        validateRequest(request);

        // 2. Tentukan Phase Type & Name
        String phaseCode = request.getPhaseCode();
        String phaseType = request.getPhaseType(); 
        String phaseName = request.getPhaseName();

        // 3. Ambil data lama
        List<SamplingSchedule> existingSchedules = samplingScheduleRepository.findByProtocolProtocolIdAndStatusOrderByRelativeMinuteAsc(protocol.getProtocolId(), EntityStatus.ACTIVE);

        // 4. Hitung Boundary (Logika JS)
        int previousEndBoundary = 0;
        String lastType = null;
        if (!existingSchedules.isEmpty()) {
            SamplingSchedule lastItem = existingSchedules.get(existingSchedules.size() - 1);
            lastType = lastItem.getPhaseType();
            
            if ("Preparation".equals(lastType) || "Finalization".equals(lastType)) {
                previousEndBoundary = lastItem.getRelativeMinute() + lastItem.getPhaseDuration();
            } else {
                previousEndBoundary = lastItem.getRelativeMinute();
            }
        }

        // 5. Tentukan Start & End (Logika JS)
        int interval = request.getTimeInterval();
        int duration = request.getPhaseDuration();
        int start = previousEndBoundary;

        if ("Finalization".equals(phaseType) && lastType != null && !"Preparation".equals(lastType) && !"Finalization".equals(lastType)) {
            start = previousEndBoundary;
        } else if (lastType != null && !"Preparation".equals(lastType) && !"Finalization".equals(lastType)) {
            start = previousEndBoundary + interval;
        }
        int end = previousEndBoundary + duration;

        // 6. Buat objek baru di memori (Tanpa Save ke DB dulu)
        List<SamplingSchedule> newSchedules = new ArrayList<>();
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        if ("Preparation".equals(phaseType) || "Finalization".equals(phaseType)) {
            if (!isDuplicate(existingSchedules, start, phaseName)) {
                newSchedules.add(buildScheduleEntity(protocol, phaseCode, phaseName, phaseType, start, duration, interval, false, false, currentUserId, now));
            }
        } else {
            for (int m = start; m <= end; m += interval) {
                if (!isDuplicate(existingSchedules, m, phaseName)) {
                    boolean isInsulinInject = ("Pre-Insulin".equals(phaseType) && m == end);
                    newSchedules.add(buildScheduleEntity(protocol, phaseCode, phaseName, phaseType, m, duration, interval, true, isInsulinInject, currentUserId, now));
                }
            }
        }

        // 7. GABUNGKAN Data Lama dan Baru untuk kalkulasi Label
        List<SamplingSchedule> fullList = new ArrayList<>();
        fullList.addAll(existingSchedules);
        fullList.addAll(newSchedules);

        // Wajib Sort agar urutan label T0, T-10, GD1, GD2 benar sesuai menit
        fullList.sort(Comparator.comparingInt(SamplingSchedule::getRelativeMinute));

        // 8. Isi Schedule Code (Recalculate) - Sekarang newSchedules sudah punya code
        recalculateAllCodes(fullList, request.getLabelPrefix());

        // 9. SIMPAN SEKALIGUS
        // Ini akan melakukan INSERT untuk yang baru dan UPDATE untuk yang lama (jika labelnya berubah)
        samplingScheduleRepository.saveAll(fullList);

        return ApiDataResponseBuilder.builder()
                .data(fullList.stream().map(this::mapToResponse).toList())
                .message("Sampling schedule berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    private SamplingSchedule buildScheduleEntity(Protocol protocol,String phaseCode, String phaseName, String phaseType, int minute, int duration, int interval, boolean bloodDraw, boolean insulinInject, Integer userId, LocalDateTime now) {
        SamplingSchedule s = SamplingSchedule.builder()
                .protocol(protocol)
                .phaseCode(phaseCode)
                .phaseName(phaseName)
                .phaseType(phaseType)
                .phaseDuration(duration)
                .timeInterval(interval)
                .relativeMinute(minute)
                .bloodRaw(bloodDraw)
                .insulinInject(insulinInject)
                .pkSampleCollection(false)
                .status(EntityStatus.ACTIVE)
                .build();
        s.setCreatedBy(userId);
        s.setUpdatedBy(userId);
        s.setCreatedAt(now);
        s.setUpdatedAt(now);
        s.setScheduleCode("-"); 
        return s;
    }

    private void recalculateAllCodes(List<SamplingSchedule> schedules, String labelPrefix) {
        String prefix = (labelPrefix == null || labelPrefix.isEmpty()) ? "GD" : labelPrefix;
        
        // Cari max minute untuk tipe pre-insulin
        int maxPreInsulinMinute = schedules.stream()
                .filter(s -> "Pre-Insulin".equals(s.getPhaseType()))
                .mapToInt(SamplingSchedule::getRelativeMinute)
                .max()
                .orElse(0);

        int gdCounter = 1;
        for (SamplingSchedule s : schedules) {
            String code = "-";
            if ("Finalization".equals(s.getPhaseType())) {
                code = "FINAL";
            } else if ("Preparation".equals(s.getPhaseType())) {
                code = "PREP";
            } else if (Boolean.TRUE.equals(s.getBloodRaw())) {
                if ("Pre-Insulin".equals(s.getPhaseType())) {
                    int offset = s.getRelativeMinute() - maxPreInsulinMinute;
                    code = "T" + (offset == 0 ? "0" : offset);
                } else {
                    code = prefix + gdCounter;
                    gdCounter++;
                }
            }
            s.setScheduleCode(code);
        }
    }

    private boolean isDuplicate(List<SamplingSchedule> existing, int minute, String phaseName) {
        return existing.stream().anyMatch(s -> s.getRelativeMinute() == minute && s.getPhaseName().equals(phaseName));
    }

    private void validateRequest(SamplingScheduleRequest request) {
        if (request.getPhaseDuration() <= 0 || request.getTimeInterval() <= 0) {
            throw new IllegalArgumentException("Durasi dan interval harus lebih dari 0");
        }
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
            recalculateSchedules(
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
    public ApiDataResponseBuilder bulkUpdateSamplingSchedules(BulkUpdateSamplingScheduleRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Tidak ada data untuk diupdate");
        }

        List<Long> ids = request.getItems().stream().map(UpdateItem::getId).toList();
        List<SamplingSchedule> schedules = samplingScheduleRepository.findAllById(ids);
        
        if (schedules.isEmpty()) {
            throw new DataNotFoundException("Data tidak ditemukan");
        }

        Long protocolId = schedules.get(0).getProtocol().getProtocolId();
        Integer userId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        // Mapping untuk update cepat
        Map<Long, UpdateItem> updateMap = request.getItems().stream()
            .collect(Collectors.toMap(UpdateItem::getId, item -> item));

        for (SamplingSchedule s : schedules) {
            UpdateItem ui = updateMap.get(s.getSamplingScheduleId());
            if (ui != null) {
                if (ui.getBloodRaw() != null) s.setBloodRaw(ui.getBloodRaw());
                if (ui.getInsulinInject() != null) s.setInsulinInject(ui.getInsulinInject());
                if (ui.getPkSampleCollection() != null) s.setPkSampleCollection(ui.getPkSampleCollection());
                
                s.setUpdatedBy(userId);
                s.setUpdatedAt(now);
            }
        }

        // SIMPAN
        samplingScheduleRepository.saveAll(schedules);

        // RECALCULATE CODES (Penting karena bloodRaw menentukan apakah dapet label GD/T atau '-')
        // Tarik semua data protocol tersebut untuk menjamin urutan label GD1, GD2 tetap konsisten
        List<SamplingSchedule> fullProtocolSchedules = samplingScheduleRepository
                .findByProtocolProtocolIdAndStatusOrderByRelativeMinuteAsc(protocolId, EntityStatus.ACTIVE);
        
        // Asumsi: Kita ambil prefix dari data yang sudah ada atau default "GD"
        String labelPrefix = fullProtocolSchedules.stream()
                .map(s -> extractPrefix(s.getScheduleCode()))
                .filter(p -> !p.equals("-") && !p.equals("PREP") && !p.equals("FINAL") && !p.startsWith("T"))
                .findFirst().orElse("GD");

        recalculateAllCodes(fullProtocolSchedules, labelPrefix);
        samplingScheduleRepository.saveAll(fullProtocolSchedules);

        return ApiDataResponseBuilder.builder()
                .message("Bulk update sampling schedule berhasil")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteSamplingSchedule(Long protocolId, String phaseCode) {
        Integer currentUserId = getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        // Kirim EntityStatus.DELETED sebagai status baru, 
        // dan EntityStatus.ACTIVE sebagai filter target.
        int affectedRows = samplingScheduleRepository.bulkSoftDeleteByProtocolAndPhase(
                protocolId, 
                phaseCode, 
                EntityStatus.DELETED, 
                EntityStatus.ACTIVE, // Pastikan ini dikirim
                now, 
                currentUserId
        );

        if (affectedRows == 0) {
            return ApiDataResponseBuilder.builder()
                    .message("Data sampling schedule tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND).build();
        }

        entityManager.flush();
        entityManager.clear();

        recalculateSchedules(protocolId);

        return ApiDataResponseBuilder.builder()
                .message("Fase berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK).build();
    }

    private void recalculateSchedules(Long protocolId) {
        // Ambil semua data aktif yang tersisa
        List<SamplingSchedule> schedules = samplingScheduleRepository
                .findByProtocolProtocolIdAndStatusOrderByRelativeMinuteAsc(protocolId, EntityStatus.ACTIVE);

        if (schedules.isEmpty()) return;

        // --- LANGKAH A: Hindari Tabrakan Index Unik (Temporary Shift) ---
        // Geser semua menit ke angka yang sangat besar sementara (misal +100.000)
        // agar saat kita set ke menit 80, tidak bentrok dengan record lain yang masih di menit 80
        for (SamplingSchedule s : schedules) {
            s.setRelativeMinute(s.getRelativeMinute() + 100000);
        }
        samplingScheduleRepository.saveAllAndFlush(schedules);

        // --- LANGKAH B: Hitung Ulang Menit sesuai Bisnis Rule ---
        String labelPrefix = schedules.stream()
                .map(s -> extractPrefix(s.getScheduleCode()))
                .filter(p -> !p.equals("-") && !p.equals("PREP") && !p.equals("FINAL") && !p.startsWith("T"))
                .findFirst().orElse("GD");

        int currentBoundary = 0;
        String lastPhaseName = null;
        int prevMinute = 0;

        for (SamplingSchedule s : schedules) {
            String currentType = s.getPhaseType();
            String currentPhaseName = s.getPhaseName();

            if ("Preparation".equalsIgnoreCase(currentType) || "Finalization".equalsIgnoreCase(currentType)) {
                s.setRelativeMinute(currentBoundary);
            } else {
                if (lastPhaseName != null && !currentPhaseName.equals(lastPhaseName)) {
                    s.setRelativeMinute(currentBoundary + s.getTimeInterval());
                } else if (lastPhaseName == null) {
                    // Kasus jika record pertama bukan Prep/Final
                    s.setRelativeMinute(s.getTimeInterval());
                } else {
                    s.setRelativeMinute(prevMinute + s.getTimeInterval());
                }
            }

            // Update Boundary
            if ("Preparation".equalsIgnoreCase(currentType) || "Finalization".equalsIgnoreCase(currentType)) {
                currentBoundary = s.getRelativeMinute() + s.getPhaseDuration();
            } else {
                currentBoundary = s.getRelativeMinute();
            }

            // Update Flag Insulin Inject
            if ("pre-insulin".equalsIgnoreCase(currentType)) {
                s.setInsulinInject(isLastRecordInPhase(schedules, s));
            } else {
                s.setInsulinInject(false);
            }

            lastPhaseName = currentPhaseName;
            prevMinute = s.getRelativeMinute();
        }

        // --- LANGKAH C: Hitung Ulang Kode (Labeling) ---
        recalculateAllCodes(schedules, labelPrefix);

        // Simpan semua perubahan final
        samplingScheduleRepository.saveAll(schedules);
    }

    private boolean isLastRecordInPhase(List<SamplingSchedule> all, SamplingSchedule current) {
        int currentIndex = all.indexOf(current);
        if (currentIndex == all.size() - 1) return true;
        SamplingSchedule next = all.get(currentIndex + 1);
        return !next.getPhaseName().equals(current.getPhaseName());
    }

    private String extractPrefix(String code) {
        if (code == null || code.isEmpty() || code.equals("-")) return "GD";
        // Ambil karakter alfabet saja dari awal string
        return code.split("[0-9]")[0].replace("-", "");
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
                .scheduleCode(detail.getScheduleCode())
                .phaseCode(detail.getPhaseCode())
                .phaseName(detail.getPhaseName())
                .phaseType(detail.getPhaseType())
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
