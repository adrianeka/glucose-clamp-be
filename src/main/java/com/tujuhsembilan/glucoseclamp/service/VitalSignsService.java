package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.VitalSignUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.VitalSignResponse;
import com.tujuhsembilan.glucoseclamp.model.VitalSign;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import com.tujuhsembilan.glucoseclamp.repository.VitalSignRepository;
import com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VitalSignsService {

    @Autowired
    private VitalSignRepository vitalSignRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllVitalSigns(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<VitalSignResponse> result = vitalSignRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
            .data(result)
                .message("Berhasil mendapatkan data tanda vital")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getVitalSignById(Integer id) {
        VitalSign vital = vitalSignRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (vital == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Berhasil mendapatkan data tanda vital")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder addVitalSign(VitalSignRequest request) {
        VitalSign vital = new VitalSign();

        // session
        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(vital::setSession);

        // measuredAt parsing
        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt;
                if (s.contains("T")) dt = LocalDateTime.parse(s);
                else dt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                vital.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        vital.setSystolic(request.getSystolic());
        vital.setDiastolic(request.getDiastolic());
        vital.setPulse(request.getPulse());
        vital.setRespiratoryRate(request.getRespiratoryRate());
        if (request.getTemperatureC() != null) vital.setTemperatureC(request.getTemperatureC());
        if (request.getSpo2() != null) vital.setSpo2(request.getSpo2());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(vital::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();
        vital.setCreatedAt(now);
        vital.setUpdatedAt(now);
        vital.setCreatedBy(currentUser);
        vital.setUpdatedBy(currentUser);
        vital.setStatus(EntityStatus.ACTIVE);

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Vital sign berhasil ditambahkan")
            .statusCode(HttpStatus.CREATED.value())
            .status(HttpStatus.CREATED)
            .build();
    }

    @Transactional
    public VitalSign save(VitalSignRequest request) {
        VitalSign vital = vitalSignRepository
            .findBySessionSessionIdAndDeletedAtIsNull(request.getSessionId())
            .orElseGet(VitalSign::new);

        // session
        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(vital::setSession);

        // measuredAt parsing
        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt;
                if (s.contains("T")) dt = LocalDateTime.parse(s);
                else dt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                vital.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        vital.setSystolic(request.getSystolic());
        vital.setDiastolic(request.getDiastolic());
        vital.setPulse(request.getPulse());
        vital.setRespiratoryRate(request.getRespiratoryRate());
        if (request.getTemperatureC() != null) vital.setTemperatureC(request.getTemperatureC());
        if (request.getSpo2() != null) vital.setSpo2(request.getSpo2());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(vital::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();

        if (vital.getVitalId() == null) {
            vital.setCreatedAt(now);
            vital.setCreatedBy(currentUser);
            vital.setStatus(EntityStatus.ACTIVE);
        }

        vital.setUpdatedAt(now);
        vital.setUpdatedBy(currentUser);

        return vitalSignRepository.save(vital);
    }

    @Transactional
    public ApiDataResponseBuilder updateVitalSign(Integer id, VitalSignUpdateRequest request) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt = s.contains("T") ? LocalDateTime.parse(s) : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                vital.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        if (request.getSystolic() != null) vital.setSystolic(request.getSystolic());
        if (request.getDiastolic() != null) vital.setDiastolic(request.getDiastolic());
        if (request.getPulse() != null) vital.setPulse(request.getPulse());
        if (request.getRespiratoryRate() != null) vital.setRespiratoryRate(request.getRespiratoryRate());
        if (request.getTemperatureC() != null) vital.setTemperatureC(request.getTemperatureC());
        if (request.getSpo2() != null) vital.setSpo2(request.getSpo2());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(vital::setAssignedByUser);

        vital.setUpdatedBy(currentUser);
        vital.setUpdatedAt(LocalDateTime.now());

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Vital sign berhasil diupdate")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteVitalSign(Integer id) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        vital.setDeletedAt(LocalDateTime.now());
        vital.setDeletedBy(currentUser);
        vital.setStatus(EntityStatus.DELETED);

        vitalSignRepository.save(vital);
        log.info("VitalSign {} berhasil dihapus (soft) oleh user {}", id, currentUser);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Vital sign berhasil dihapus")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateVitalSignStatus(Integer id, VitalSignStatusUpdateRequest request) {
        Optional<VitalSign> opt = vitalSignRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data tanda vital tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        VitalSign vital = opt.get();
        Integer currentUser = getCurrentUserId();

        vital.setStatus(request.getStatus());
        vital.setUpdatedBy(currentUser);
        vital.setUpdatedAt(LocalDateTime.now());

        vitalSignRepository.save(vital);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(vital))
            .message("Status vital sign berhasil diupdate")
            .statusCode(HttpStatus.OK.value())
            .status(HttpStatus.OK)
            .build();
    }

    public ApiDataResponseBuilder searchVitalSigns(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<VitalSignResponse> result = (keyword == null || keyword.isBlank())
                ? vitalSignRepository.findAllActive(pageable).map(this::mapToResponse)
                : vitalSignRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari tanda vital")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public VitalSignResponse mapToResponse(VitalSign vital) {
        // 1. Simpan konfigurasi matching strategy bawaan aplikasi Anda dulu
        var isiStrategyLama = modelMapper.getConfiguration().getMatchingStrategy();
        
        // 2. Paksa ModelMapper menggunakan mode STRICT agar tidak menebak field asal-asalan
        modelMapper.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);

        try {
            // 3. Lakukan mapping otomatis untuk field yang aman (systolic, diastolic, pulse, dll)
            VitalSignResponse response = modelMapper.map(vital, VitalSignResponse.class);
            
            // 4. Petakan secara manual field relasi yang ambigu secara eksplisit
            response.setSessionId(vital.getSession() == null ? null : vital.getSession().getSessionId());
            response.setAssignedBy(vital.getAssignedByUser() == null ? null : vital.getAssignedByUser().getUserId());
            
            return response;
        } finally {
            // 5. Kembalikan konfigurasi strategy ke semula agar tidak mengganggu service lain
            modelMapper.getConfiguration().setMatchingStrategy(isiStrategyLama);
        }
    }
}
