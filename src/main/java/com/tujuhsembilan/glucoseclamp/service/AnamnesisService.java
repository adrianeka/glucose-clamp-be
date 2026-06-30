package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.AnamnesisResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.model.Anamnesis;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.AnamnesisRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@Service
public class AnamnesisService {

    @Autowired
    private AnamnesisRepository anamnesisRepository;

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

    public ApiDataResponseBuilder getAllAnamneses(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<AnamnesisResponse> result = anamnesisRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data anamnesis")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getAnamnesisById(Integer id) {
        Anamnesis ana = anamnesisRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (ana == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data anamnesis tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(ana))
                .message("Berhasil mendapatkan data anamnesis")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addAnamnesis(AnamnesisRequest request) {
        Anamnesis ana = new Anamnesis();

        // session
        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(ana::setSession);

        // date parsing
        try {
            if (request.getDate() != null) {
                LocalDate d = LocalDate.parse(request.getDate());
                ana.setDate(d);
            }
        } catch (DateTimeParseException ignored) {
        }

        ana.setChiefComplaint(request.getChiefComplaint());
        ana.setMedicalHistory(request.getMedicalHistory());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(ana::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();
        ana.setCreatedAt(now);
        ana.setUpdatedAt(now);
        ana.setCreatedBy(currentUser);
        ana.setUpdatedBy(currentUser);
        ana.setStatus(EntityStatus.ACTIVE);

        anamnesisRepository.save(ana);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(ana))
                .message("Anamnesis berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public Anamnesis save(AnamnesisRequest request) {
        Anamnesis ana = anamnesisRepository
            .findBySessionIdAndDeletedAtIsNull(request.getSessionId())
            .orElseGet(Anamnesis::new);

        // session
        sessionRepository.findByIdAndDeletedAtIsNull(request.getSessionId()).ifPresent(ana::setSession);

        // date parsing
        try {
            if (request.getDate() != null) {
                LocalDate d = LocalDate.parse(request.getDate());
                ana.setDate(d);
            }
        } catch (DateTimeParseException ignored) {
        }

        ana.setChiefComplaint(request.getChiefComplaint());
        ana.setMedicalHistory(request.getMedicalHistory());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(ana::setAssignedByUser);

        LocalDateTime now = LocalDateTime.now();
        Integer currentUser = getCurrentUserId();

        if (ana.getAnamnesisId() == null) {
            ana.setCreatedAt(now);
            ana.setCreatedBy(currentUser);
            ana.setStatus(EntityStatus.ACTIVE);
        }

        ana.setUpdatedAt(now);
        ana.setUpdatedBy(currentUser);

        return anamnesisRepository.save(ana);
    }

    @Transactional
    public ApiDataResponseBuilder updateAnamnesis(Integer id, AnamnesisUpdateRequest request) {
        Optional<Anamnesis> opt = anamnesisRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data anamnesis tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Anamnesis ana = opt.get();
        Integer currentUser = getCurrentUserId();

        if (request.getChiefComplaint() != null) ana.setChiefComplaint(request.getChiefComplaint());
        if (request.getMedicalHistory() != null) ana.setMedicalHistory(request.getMedicalHistory());
        if (request.getAssignedBy() != null) userRepository.findByIdAndDeletedAtIsNull(request.getAssignedBy()).ifPresent(ana::setAssignedByUser);

        ana.setUpdatedBy(currentUser);
        ana.setUpdatedAt(LocalDateTime.now());

        anamnesisRepository.save(ana);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(ana))
                .message("Anamnesis berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteAnamnesis(Integer id) {
        Optional<Anamnesis> opt = anamnesisRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data anamnesis tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Anamnesis ana = opt.get();
        Integer currentUser = getCurrentUserId();

        ana.setDeletedAt(LocalDateTime.now());
        ana.setDeletedBy(currentUser);
        ana.setStatus(EntityStatus.DELETED);

        anamnesisRepository.save(ana);
        log.info("Anamnesis {} berhasil dihapus (soft) oleh user {}", id, currentUser);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(ana))
                .message("Anamnesis berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateAnamnesisStatus(Integer id, AnamnesisStatusUpdateRequest request) {
        Optional<Anamnesis> opt = anamnesisRepository.findById(id);
        if (opt.isEmpty() || EntityStatus.DELETED.equals(opt.get().getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data anamnesis tidak ditemukan")
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

        Anamnesis ana = opt.get();
        Integer currentUser = getCurrentUserId();

        ana.setStatus(request.getStatus());
        ana.setUpdatedBy(currentUser);
        ana.setUpdatedAt(LocalDateTime.now());

        anamnesisRepository.save(ana);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(ana))
                .message("Status anamnesis berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchAnamneses(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<AnamnesisResponse> result = (keyword == null || keyword.isBlank())
                ? anamnesisRepository.findAllActive(pageable).map(this::mapToResponse)
                : anamnesisRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari anamnesis")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public AnamnesisResponse mapToResponse(Anamnesis ana) {
        return AnamnesisResponse.builder()
                .anamnesisId(ana.getAnamnesisId())
                .sessionId(
                        ana.getSession() != null
                                ? ana.getSession().getSessionId()
                                : null
                )
                .date(ana.getDate())
                .chiefComplaint(ana.getChiefComplaint())
                .medicalHistory(ana.getMedicalHistory())
                .assignedBy(
                        ana.getAssignedByUser() != null
                                ? ana.getAssignedByUser().getUserId()
                                : null
                )
                .createdAt(ana.getCreatedAt())
                .createdBy(ana.getCreatedBy())
                .updatedAt(ana.getUpdatedAt())
                .updatedBy(ana.getUpdatedBy())
                .deletedAt(ana.getDeletedAt())
                .deletedBy(ana.getDeletedBy())
                .status(ana.getStatus())
                .build();
    }
}
