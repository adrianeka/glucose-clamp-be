package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.AnthropometryResponse;
import com.tujuhsembilan.glucoseclamp.model.Anamnesis;
import com.tujuhsembilan.glucoseclamp.model.Anthropometry;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.AnthropometryRepository;
import com.tujuhsembilan.glucoseclamp.repository.SessionRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
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
import java.util.Optional;

@Slf4j
@Service
public class AnthropometryService {

    @Autowired
    private AnthropometryRepository anthropometryRepository;

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

    public ApiDataResponseBuilder getAllAnthropometries(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<AnthropometryResponse> result = anthropometryRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data antropometri")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getAnthropometryById(Integer id) {
        Anthropometry anthro = anthropometryRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (anthro == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data antropometri tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(anthro))
                .message("Berhasil mendapatkan data antropometri")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addAnthropometry(AnthropometryRequest request) {
        Anthropometry anthro = new Anthropometry();

        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(anthro::setSession);

        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt = s.contains("T") ? LocalDateTime.parse(s) : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                anthro.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        anthro.setWeightKg(request.getWeightKg());
        anthro.setHeightCm(request.getHeightCm());
        anthro.setBmi(request.getBmi());
        anthro.setWaistCircumferenceCm(request.getWaistCircumferenceCm());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(anthro::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();
        anthro.setCreatedAt(now);
        anthro.setUpdatedAt(now);
        anthro.setCreatedBy(currentUser);
        anthro.setUpdatedBy(currentUser);
        anthro.setStatus(EntityStatus.ACTIVE);

        anthropometryRepository.save(anthro);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(anthro))
                .message("Anthropometry berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public Anthropometry save(AnthropometryRequest request) {
        Anthropometry anthro = anthropometryRepository
            .findBySessionIdAndDeletedAtIsNull(request.getSessionId())
            .orElseGet(Anthropometry::new);

        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(anthro::setSession);

        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt = s.contains("T") ? LocalDateTime.parse(s) : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                anthro.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        anthro.setWeightKg(request.getWeightKg());
        anthro.setHeightCm(request.getHeightCm());
        anthro.setBmi(request.getBmi());
        anthro.setWaistCircumferenceCm(request.getWaistCircumferenceCm());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(anthro::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();

        if (anthro.getAnthroId() == null) {
            anthro.setCreatedAt(now);
            anthro.setCreatedBy(currentUser);
            anthro.setStatus(EntityStatus.ACTIVE);
        }

        anthro.setUpdatedAt(now);
        anthro.setUpdatedBy(currentUser);

        return anthropometryRepository.save(anthro);
    }

    @Transactional
    public ApiDataResponseBuilder updateAnthropometry(Integer id, AnthropometryUpdateRequest request) {
        Optional<Anthropometry> opt = anthropometryRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data antropometri tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Anthropometry anthro = opt.get();
        Integer currentUser = getCurrentUserId();

        try {
            if (request.getMeasuredAt() != null) {
                String s = request.getMeasuredAt();
                LocalDateTime dt = s.contains("T") ? LocalDateTime.parse(s) : LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"));
                anthro.setMeasuredAt(dt);
            }
        } catch (DateTimeParseException ignored) {
        }

        if (request.getWeightKg() != null) anthro.setWeightKg(request.getWeightKg());
        if (request.getHeightCm() != null) anthro.setHeightCm(request.getHeightCm());
        if (request.getBmi() != null) anthro.setBmi(request.getBmi());
        if (request.getWaistCircumferenceCm() != null) anthro.setWaistCircumferenceCm(request.getWaistCircumferenceCm());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(anthro::setAssignedByUser);

        anthro.setUpdatedBy(currentUser);
        anthro.setUpdatedAt(LocalDateTime.now());

        anthropometryRepository.save(anthro);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(anthro))
                .message("Anthropometry berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteAnthropometry(Integer id) {
        Optional<Anthropometry> opt = anthropometryRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data antropometri tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Anthropometry anthro = opt.get();
        Integer currentUser = getCurrentUserId();

        anthro.setDeletedAt(LocalDateTime.now());
        anthro.setDeletedBy(currentUser);
        anthro.setStatus(EntityStatus.DELETED);

        anthropometryRepository.save(anthro);
        log.info("Anthropometry {} berhasil dihapus (soft) oleh user {}", id, currentUser);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(anthro))
                .message("Anthropometry berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateAnthropometryStatus(Integer id, AnthropometryStatusUpdateRequest request) {
        Optional<Anthropometry> opt = anthropometryRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data antropometri tidak ditemukan")
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

        Anthropometry anthro = opt.get();
        Integer currentUser = getCurrentUserId();

        anthro.setStatus(request.getStatus());
        anthro.setUpdatedBy(currentUser);
        anthro.setUpdatedAt(LocalDateTime.now());

        anthropometryRepository.save(anthro);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(anthro))
                .message("Status anthropometry berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchAnthropometries(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<AnthropometryResponse> result = (keyword == null || keyword.isBlank())
                ? anthropometryRepository.findAllActive(pageable).map(this::mapToResponse)
                : anthropometryRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari antropometri")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public AnthropometryResponse mapToResponse(Anthropometry anthro) {
        var isiStrategyLama = modelMapper.getConfiguration().getMatchingStrategy();
        modelMapper.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);

        try {
            AnthropometryResponse response = modelMapper.map(anthro, AnthropometryResponse.class);
            response.setSessionId(anthro.getSession() == null ? null : anthro.getSession().getSessionId());
            response.setAssignedBy(anthro.getAssignedByUser() == null ? null : anthro.getAssignedByUser().getUserId());
            return response;
        } finally {
            modelMapper.getConfiguration().setMatchingStrategy(isiStrategyLama);
        }
    }
}
