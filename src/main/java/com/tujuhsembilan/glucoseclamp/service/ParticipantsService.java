package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.ParticipantResponse;
import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.model.Participant;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.ParticipantRepository;
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
import java.time.Period;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class ParticipantsService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    private String generateNextParticipantId() {
        Optional<Participant> lastParticipant = participantRepository.findTopByOrderByParticipantIdDesc();

        if (lastParticipant.isEmpty() || lastParticipant.get().getParticipantId() == null) {
            return "PAT-001";
        }

        String lastId = lastParticipant.get().getParticipantId();
        if (!lastId.startsWith("PAT-")) {
            return "PAT-001";
        }

        try {
            int numericPart = Integer.parseInt(lastId.substring(4));
            return String.format("PAT-%03d", numericPart + 1);
        } catch (NumberFormatException e) {
            return "PAT-001";
        }
    }

    private String calculateAge(LocalDate dob) {
        if (dob == null) {
            return null;
        }
        Period period = Period.between(dob, LocalDate.now());
        return period.getYears() + "y " + period.getMonths() + "m";
    }
    
    public ApiDataResponseBuilder getAllParticipants(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ParticipantResponse> result = participantRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data partisipan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getParticipantById(String participantId) {
        Participant participant = participantRepository.findByIdAndDeletedAtIsNull(participantId).orElse(null);

        if (participant == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data partisipan tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(participant))
                .message("Berhasil mendapatkan data partisipan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchParticipantsByKeyword(String keyword, int pageNumber, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Keyword pencarian tidak boleh kosong")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ParticipantResponse> result = participantRepository.searchByKeyword(keyword.trim(), pageable)
                .map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data partisipan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addParticipant(ParticipantRequest request) {
        if (participantRepository.findByMedicalRecordNoAndDeletedAtIsNull(request.getMedicalRecordNo()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Nomor rekam medis sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        String newParticipantId = generateNextParticipantId();

        Integer currentUserId = getCurrentUserId();

        Participant participant = Participant.builder()
                .participantId(newParticipantId)
                .medicalRecordNo(request.getMedicalRecordNo())
                .name(request.getName())
                .gender(request.getGender())
                .dob(LocalDate.parse(request.getDob()))
                .numberPhone(request.getNumberPhone())
                .build();

        LocalDateTime now = LocalDateTime.now();
        participant.setCreatedAt(now);
        participant.setUpdatedAt(now);
        participant.setCreatedBy(currentUserId);
        participant.setUpdatedBy(currentUserId);
        participant.setStatus(EntityStatus.ACTIVE);

        participantRepository.save(participant);
        log.info("Participant berhasil ditambahkan: {} oleh user {}", participant.getParticipantId(), currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(participant))
                .message("Partisipan berhasil ditambahkan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateParticipant(String participantId, ParticipantUpdateRequest request) {
        Participant participant = participantRepository.findById(participantId).orElse(null);

        if (participant == null || EntityStatus.INACTIVE.equals(participant.getStatus()) || EntityStatus.DELETED.equals(participant.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data partisipan tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        if (request.getMedicalRecordNo() != null) {
            participant.setMedicalRecordNo(request.getMedicalRecordNo());
        }
        if (request.getName() != null) {
            participant.setName(request.getName());
        }
        if (request.getGender() != null) {
            participant.setGender(request.getGender());
        }
        if (request.getDob() != null) {
            participant.setDob(LocalDate.parse(request.getDob()));
        }
        if (request.getNumberPhone() != null) {
            participant.setNumberPhone(request.getNumberPhone());
        }
        participant.setUpdatedBy(currentUserId);

        participantRepository.save(participant);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(participant))
                .message("Data partisipan berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateParticipantStatus(String participantId, ParticipantStatusUpdateRequest request) {
        Participant participant = participantRepository.findById(participantId).orElse(null);

        if (participant == null || EntityStatus.DELETED.equals(participant.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data partisipan tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status partisipan tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        participant.setStatus(request.getStatus());
        participant.setUpdatedBy(currentUserId);
        participant.setUpdatedAt(LocalDateTime.now());

        participantRepository.save(participant);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(participant))
                .message("Status partisipan berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteParticipant(String participantId) {
        Participant participant = participantRepository.findById(participantId).orElse(null);

        if (participant == null || EntityStatus.DELETED.equals(participant.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data partisipan tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        participant.setDeletedAt(LocalDateTime.now());
        participant.setDeletedBy(currentUserId);
        participant.setStatus(EntityStatus.DELETED);

        participantRepository.save(participant);
        log.info("Participant {} berhasil dihapus (soft delete) oleh user {}", participantId, currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(participant))
                .message("Partisipan berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private ParticipantResponse mapToResponse(Participant participant) {
        ParticipantResponse response = modelMapper.map(participant, ParticipantResponse.class);

        response.setStatus(participant.getStatus() == null ? null : participant.getStatus().name());
        response.setAge(calculateAge(participant.getDob()));
        response.setCreatedAt(participant.getCreatedAt() == null ? null : participant.getCreatedAt().toString());
        response.setUpdatedAt(participant.getUpdatedAt() == null ? null : participant.getUpdatedAt().toString());

        if (participant.getCreatedBy() != null) {
            userRepository.findById(participant.getCreatedBy())
                    .ifPresent(user -> response.setCreatedByName(user.getName()));
        } else {
            response.setCreatedByName("System");
        }

        if (participant.getUpdatedBy() != null) {
            userRepository.findById(participant.getUpdatedBy())
                    .ifPresent(user -> response.setUpdatedByName(user.getName()));
        } else {
            response.setUpdatedByName("System");
        }

        return response;
    }

}
