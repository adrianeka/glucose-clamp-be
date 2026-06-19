package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationBulkPriorityRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationPriorityItemRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.PhaseConfigurationResponse;
import com.tujuhsembilan.glucoseclamp.model.PhaseConfiguration;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.PhaseConfigurationRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

@Service
@RequiredArgsConstructor
public class PhaseConfigurationService {

    private final PhaseConfigurationRepository phaseConfigurationRepository;
    private final ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        try {
            var principal = authentication.getPrincipal();
            var userDetails = (com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement) principal;
            return userDetails.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    public ApiDataResponseBuilder getAllPhaseConfigurations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<PhaseConfigurationResponse> result = phaseConfigurationRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data phase configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getPhaseConfigurationById(Long id) {
        Optional<PhaseConfiguration> phaseConfiguration = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (phaseConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data phase configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration.get()))
                .message("Berhasil mendapatkan data phase configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addPhaseConfiguration(PhaseConfigurationRequest request) {
        String normalizedCode = normalize(request.getPhaseConfCode());
        Optional<PhaseConfiguration> existing = phaseConfigurationRepository.findByPhaseConfCodeAndDeletedAtIsNull(normalizedCode);
        if (existing.isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("phase_conf_code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        PhaseConfiguration phaseConfiguration = PhaseConfiguration.builder()
                .phaseConfPriority(request.getPhaseConfPriority())
                .phaseConfCode(normalizedCode)
                .phaseConfName(request.getPhaseConfName().trim())
                .phaseConfType(request.getPhaseConfType().trim())
                .build();
        phaseConfiguration.setStatus(EntityStatus.ACTIVE);
        phaseConfiguration.setCreatedBy(currentUserId);
        phaseConfiguration.setUpdatedBy(currentUserId);

        phaseConfigurationRepository.save(phaseConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration))
                .message("Phase configuration berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePhaseConfiguration(Long id, PhaseConfigurationRequest request) {
        Optional<PhaseConfiguration> existingPhaseConfiguration = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingPhaseConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data phase configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        PhaseConfiguration phaseConfiguration = existingPhaseConfiguration.get();
        String normalizedCode = normalize(request.getPhaseConfCode());
        Optional<PhaseConfiguration> duplicateCode = phaseConfigurationRepository.findByPhaseConfCodeAndDeletedAtIsNull(normalizedCode);
        if (duplicateCode.isPresent() && !duplicateCode.get().getPhaseConfId().equals(phaseConfiguration.getPhaseConfId())) {
            return ApiDataResponseBuilder.builder()
                    .message("phase_conf_code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer newPriority = request.getPhaseConfPriority();
        Integer oldPriority = phaseConfiguration.getPhaseConfPriority();
        
        if (newPriority != null && !oldPriority.equals(newPriority)) {
            // Set ke nilai negatif berbasis ID unik untuk membebaskan nilai prioritas lama
            phaseConfiguration.setPhaseConfPriority(-1 * phaseConfiguration.getPhaseConfId().intValue());
            phaseConfigurationRepository.saveAndFlush(phaseConfiguration);

            shiftPriorities(phaseConfiguration.getPhaseConfId(), oldPriority, newPriority);
            phaseConfigurationRepository.flush(); // Tulis perubahan ke DB
        }

        phaseConfiguration.setPhaseConfPriority(newPriority);
        phaseConfiguration.setPhaseConfCode(normalizedCode);
        phaseConfiguration.setPhaseConfName(request.getPhaseConfName().trim());
        phaseConfiguration.setPhaseConfType(request.getPhaseConfType().trim());
        phaseConfiguration.setUpdatedBy(getCurrentUserId());
        phaseConfigurationRepository.save(phaseConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration))
                .message("Phase configuration berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePhaseConfigurationsBulkPriority(PhaseConfigurationBulkPriorityRequest request) {
        List<PhaseConfigurationPriorityItemRequest> items = request.getPriorities();

        // Langkah 1: Ubah semua prioritas sementara ke nilai negatif berbasis ID unik (Bukan nilai Priority)
        for (PhaseConfigurationPriorityItemRequest item : items) {
            Optional<PhaseConfiguration> existing = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(item.getId());
            if (existing.isPresent()) {
                PhaseConfiguration phase = existing.get();
                // Menggunakan ID unik sebagai bibit pengali negatif
                phase.setPhaseConfPriority(-1 * phase.getPhaseConfId().intValue());
                phaseConfigurationRepository.save(phase);
            }
        }
        
        phaseConfigurationRepository.flush();

        // Langkah 2: Set ke nilai prioritas akhir yang baru secara bertahap
        for (PhaseConfigurationPriorityItemRequest item : items) {
            Optional<PhaseConfiguration> existing = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(item.getId());
            if (existing.isPresent()) {
                PhaseConfiguration phase = existing.get();
                phase.setPhaseConfPriority(item.getPriority());
                phase.setUpdatedBy(getCurrentUserId());
                phaseConfigurationRepository.save(phase);
            }
        }
        
        phaseConfigurationRepository.flush();

        return ApiDataResponseBuilder.builder()
                .message("Daftar priority phase configuration berhasil diperbarui")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePhaseConfigurationPriority(Long id, com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationPriorityRequest request) {
        Optional<PhaseConfiguration> existingPhaseConfiguration = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingPhaseConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data phase configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer newPriority = request.getPhaseConfPriority();
        if (newPriority == null || newPriority < 1) {
            return ApiDataResponseBuilder.builder()
                    .message("phase_conf_priority harus lebih besar dari 0")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        PhaseConfiguration phaseConfiguration = existingPhaseConfiguration.get();
        Integer oldPriority = phaseConfiguration.getPhaseConfPriority();

        if (oldPriority.equals(newPriority)) {
            return ApiDataResponseBuilder.builder()
                    .data(mapToResponse(phaseConfiguration))
                    .message("Phase configuration priority tidak berubah")
                    .statusCode(HttpStatus.OK.value())
                    .status(HttpStatus.OK)
                    .build();
        }

        Integer maxPriority = phaseConfigurationRepository.findMaxPriority();
        if (newPriority > maxPriority) {
            newPriority = maxPriority;
        }

        // 1. Set priority item utama ke nilai negatif sementara berbasis ID unik untuk mengosongkan slot lamanya
        phaseConfiguration.setPhaseConfPriority(-1 * phaseConfiguration.getPhaseConfId().intValue());
        phaseConfigurationRepository.saveAndFlush(phaseConfiguration);

        // 2. Lakukan pergeseran (shift) pada item-item lainnya
        shiftPriorities(id, oldPriority, newPriority);
        phaseConfigurationRepository.flush(); // Pastikan perubahan shift tertulis di DB

        // 3. Set priority item utama ke nilai baru
        phaseConfiguration.setPhaseConfPriority(newPriority);
        phaseConfiguration.setUpdatedBy(getCurrentUserId());
        phaseConfigurationRepository.saveAndFlush(phaseConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration))
                .message("Priority phase configuration berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deletePhaseConfiguration(Long id) {
        Optional<PhaseConfiguration> existingPhaseConfiguration = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingPhaseConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data phase configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        PhaseConfiguration phaseConfiguration = existingPhaseConfiguration.get();
        Integer currentUserId = getCurrentUserId();
        Integer deletedPriority = phaseConfiguration.getPhaseConfPriority();

        phaseConfiguration.setPhaseConfPriority(-1 * phaseConfiguration.getPhaseConfId().intValue());

        phaseConfiguration.setDeletedAt(LocalDateTime.now());
        phaseConfiguration.setDeletedBy(currentUserId);
        phaseConfiguration.setStatus(EntityStatus.DELETED);
        phaseConfiguration.setUpdatedBy(currentUserId);
        phaseConfigurationRepository.saveAndFlush(phaseConfiguration);

        if (deletedPriority != null && deletedPriority > 0) {
            closePriorityGaps(deletedPriority);
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration))
                .message("Phase configuration berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePhaseConfigurationStatus(Long id, UpdateStatusRequest request) {
        Optional<PhaseConfiguration> existingPhaseConfiguration = phaseConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingPhaseConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data phase configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        PhaseConfiguration phaseConfiguration = existingPhaseConfiguration.get();
        Integer currentUserId = getCurrentUserId();
        Integer deletedPriority = phaseConfiguration.getPhaseConfPriority();

        phaseConfiguration.setStatus(request.getStatus());
        phaseConfiguration.setUpdatedBy(currentUserId);
        phaseConfiguration.setUpdatedAt(LocalDateTime.now());

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            phaseConfiguration.setPhaseConfPriority(-1 * phaseConfiguration.getPhaseConfId().intValue());
            phaseConfiguration.setDeletedAt(LocalDateTime.now());
            phaseConfiguration.setDeletedBy(currentUserId);
            phaseConfigurationRepository.saveAndFlush(phaseConfiguration);

            if (deletedPriority != null && deletedPriority > 0) {
                closePriorityGaps(deletedPriority);
            }
        } else {
            phaseConfigurationRepository.save(phaseConfiguration);
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(phaseConfiguration))
                .message("Status phase configuration berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchPhaseConfigurations(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<PhaseConfigurationResponse> result;
        if (keyword == null || keyword.isBlank()) {
            result = phaseConfigurationRepository.findAllActive(pageable).map(this::mapToResponse);
        } else {
            result = phaseConfigurationRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);
        }

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari phase configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private void shiftPriorities(Long excludedId, Integer oldPriority, Integer newPriority) {
        Integer minPriority;
        Integer maxRange;
        int shiftOffset;

        if (oldPriority < newPriority) {
            minPriority = oldPriority + 1;
            maxRange = newPriority;
            shiftOffset = -1;
        } else {
            minPriority = newPriority;
            maxRange = oldPriority - 1;
            shiftOffset = 1;
        }

        List<PhaseConfiguration> itemsToShift = phaseConfigurationRepository.findByPriorityBetweenAndNotId(minPriority, maxRange, excludedId);
        
        // Map lokal untuk mencatat nilai prioritas asli sebelum dirubah ke negatif
        Map<Long, Integer> originalPriorityMap = new HashMap<>();

        // Langkah A: Catat prioritas asli, lalu set sementara ke nilai negatif berbasis ID (dijamin 100% unik)
        for (PhaseConfiguration item : itemsToShift) {
            originalPriorityMap.put(item.getPhaseConfId(), item.getPhaseConfPriority());
            
            // Pengali negatif menggunakan ID untuk mengeliminasi resiko tumpang tindih nilai
            item.setPhaseConfPriority(-1 * item.getPhaseConfId().intValue());
            phaseConfigurationRepository.save(item);
        }
        phaseConfigurationRepository.flush();

        // Langkah B: Set ke nilai asli yang sudah digeser menggunakan referensi Map lokal
        for (PhaseConfiguration item : itemsToShift) {
            Integer originalPriority = originalPriorityMap.get(item.getPhaseConfId());
            if (originalPriority != null) {
                item.setPhaseConfPriority(originalPriority + shiftOffset);
                phaseConfigurationRepository.save(item);
            }
        }
        phaseConfigurationRepository.flush();
    }
    
    private void closePriorityGaps(Integer deletedPriority) {
        List<PhaseConfiguration> activeItemsToShift = phaseConfigurationRepository
                .findAll().stream()
                .filter(p -> p.getDeletedAt() == null && p.getPhaseConfPriority() > deletedPriority)
                .sorted(java.util.Comparator.comparing(PhaseConfiguration::getPhaseConfPriority))
                .toList();

        for (PhaseConfiguration item : activeItemsToShift) {
            item.setPhaseConfPriority(item.getPhaseConfPriority() - 1);
        }
        phaseConfigurationRepository.saveAll(activeItemsToShift);
        phaseConfigurationRepository.flush();
    }

    private PhaseConfigurationResponse mapToResponse(PhaseConfiguration phaseConfiguration) {
        PhaseConfigurationResponse response = modelMapper.map(phaseConfiguration, PhaseConfigurationResponse.class);
        response.setStatus(phaseConfiguration.getStatus() == null ? null : phaseConfiguration.getStatus().name());
        return response;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}