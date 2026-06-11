package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.GlobalConfigurationRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.GlobalConfigurationResponse;
import com.tujuhsembilan.glucoseclamp.model.GlobalConfiguration;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.GlobalConfigurationRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
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
public class GlobalConfigurationService {

    private final GlobalConfigurationRepository globalConfigurationRepository;
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

    public ApiDataResponseBuilder getAllGlobalConfigurations(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<GlobalConfigurationResponse> result = globalConfigurationRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data global configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getGlobalConfigurationById(BigInteger id) {
        Optional<GlobalConfiguration> globalConfiguration = globalConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (globalConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data global configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(globalConfiguration.get()))
                .message("Berhasil mendapatkan data global configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addGlobalConfiguration(GlobalConfigurationRequest request) {
        String normalizedCode = normalize(request.getGconfCode());
        Optional<GlobalConfiguration> existing = globalConfigurationRepository.findByCodeAndDeletedAtIsNull(normalizedCode);
        if (existing.isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("gconf_code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();
        GlobalConfiguration globalConfiguration = GlobalConfiguration.builder()
                .gconfCode(normalizedCode)
                .gconfValue(request.getGconfValue().trim())
                .build();
        globalConfiguration.setStatus(EntityStatus.ACTIVE);
        globalConfiguration.setCreatedBy(currentUserId);
        globalConfiguration.setUpdatedBy(currentUserId);

        globalConfigurationRepository.save(globalConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(globalConfiguration))
                .message("Global configuration berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateGlobalConfiguration(BigInteger id, GlobalConfigurationRequest request) {
        Optional<GlobalConfiguration> existingGlobalConfiguration = globalConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingGlobalConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data global configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        GlobalConfiguration globalConfiguration = existingGlobalConfiguration.get();
        String normalizedCode = normalize(request.getGconfCode());
        Optional<GlobalConfiguration> duplicateCode = globalConfigurationRepository.findByCodeAndDeletedAtIsNull(normalizedCode);
        if (duplicateCode.isPresent() && !duplicateCode.get().getGconfId().equals(globalConfiguration.getGconfId())) {
            return ApiDataResponseBuilder.builder()
                    .message("gconf_code sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        globalConfiguration.setGconfCode(normalizedCode);
        globalConfiguration.setGconfValue(request.getGconfValue().trim());
        globalConfiguration.setUpdatedBy(getCurrentUserId());
        globalConfigurationRepository.save(globalConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(globalConfiguration))
                .message("Global configuration berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteGlobalConfiguration(BigInteger id) {
        Optional<GlobalConfiguration> existingGlobalConfiguration = globalConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingGlobalConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data global configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        GlobalConfiguration globalConfiguration = existingGlobalConfiguration.get();
        Integer currentUserId = getCurrentUserId();
        globalConfiguration.setDeletedAt(LocalDateTime.now());
        globalConfiguration.setDeletedBy(currentUserId);
        globalConfiguration.setStatus(EntityStatus.DELETED);
        globalConfiguration.setUpdatedBy(currentUserId);
        globalConfigurationRepository.save(globalConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(globalConfiguration))
                .message("Global configuration berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateGlobalConfigurationStatus(BigInteger id, UpdateStatusRequest request) {
        Optional<GlobalConfiguration> existingGlobalConfiguration = globalConfigurationRepository.findByIdAndDeletedAtIsNull(id);
        if (existingGlobalConfiguration.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data global configuration tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        GlobalConfiguration globalConfiguration = existingGlobalConfiguration.get();
        Integer currentUserId = getCurrentUserId();
        globalConfiguration.setStatus(request.getStatus());
        globalConfiguration.setUpdatedBy(currentUserId);
        globalConfiguration.setUpdatedAt(LocalDateTime.now());
        if (EntityStatus.DELETED.equals(request.getStatus())) {
            globalConfiguration.setDeletedAt(LocalDateTime.now());
            globalConfiguration.setDeletedBy(currentUserId);
        }
        globalConfigurationRepository.save(globalConfiguration);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(globalConfiguration))
                .message("Status global configuration berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchGlobalConfigurations(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<GlobalConfigurationResponse> result;
        if (keyword == null || keyword.isBlank()) {
            result = globalConfigurationRepository.findAllActive(pageable).map(this::mapToResponse);
        } else {
            result = globalConfigurationRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);
        }

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari global configuration")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private GlobalConfigurationResponse mapToResponse(GlobalConfiguration globalConfiguration) {
        GlobalConfigurationResponse response = modelMapper.map(globalConfiguration, GlobalConfigurationResponse.class);
        response.setStatus(globalConfiguration.getStatus() == null ? null : globalConfiguration.getStatus().name());
        return response;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
