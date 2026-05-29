package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.PatientRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.PatientResponse;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.model.Patient;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.PatientRepository;
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
import java.util.Optional;

@Slf4j
@Service
public class PatientsService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    private String generateNextPatientId() {
        Optional<Patient> lastPatient = patientRepository.findTopByOrderByPatientIdDesc();

        if (lastPatient.isEmpty() || lastPatient.get().getPatientId() == null) {
            return "PAT-001";
        }

        String lastId = lastPatient.get().getPatientId();
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

    public ApiDataResponseBuilder getAllPatients(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<PatientResponse> result = patientRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data pasien")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getPatientById(String patientId) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(patientId).orElse(null);

        if (patient == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(patient))
                .message("Berhasil mendapatkan data pasien")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchPatientsByKeyword(String keyword, int pageNumber, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiDataResponseBuilder.builder()
                .message("Keyword pencarian tidak boleh kosong")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<PatientResponse> result = patientRepository.searchByKeyword(keyword.trim(), pageable)
            .map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
        .data(result)
        .message("Berhasil mendapatkan data pasien")
        .statusCode(HttpStatus.OK.value())
        .status(HttpStatus.OK)
        .build();
    }
            

    @Transactional
    public ApiDataResponseBuilder addPatient(PatientRequest request) {
        if (patientRepository.findByMedicalRecordNoAndDeletedAtIsNull(request.getMedicalRecordNo()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Nomor rekam medis sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        String newPatientId = generateNextPatientId();

        Integer currentUserId = getCurrentUserId();

        Patient patient = Patient.builder()
                .patientId(newPatientId)
                .medicalRecordNo(request.getMedicalRecordNo())
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .dob(LocalDate.parse(request.getDob()))
                .numberPhone(request.getNumberPhone())
                .build();

        LocalDateTime now = LocalDateTime.now();
        patient.setCreatedAt(now);
        patient.setUpdatedAt(now);
        patient.setCreatedBy(currentUserId);
        patient.setUpdatedBy(currentUserId);
        patient.setStatus(EntityStatus.ACTIVE);

        patientRepository.save(patient);
        log.info("Patient berhasil ditambahkan: {} oleh user {}", patient.getPatientId(), currentUserId);

        return ApiDataResponseBuilder.builder()
        .data(mapToResponse(patient))
                .message("Pasien berhasil ditambahkan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePatient(String patientId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId).orElse(null);

        if (patient == null || EntityStatus.INACTIVE.equals(patient.getStatus()) || EntityStatus.DELETED.equals(patient.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        if (request.getMedicalRecordNo() != null) patient.setMedicalRecordNo(request.getMedicalRecordNo());
        if (request.getName() != null) patient.setName(request.getName());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getDob() != null) patient.setDob(LocalDate.parse(request.getDob()));
        if (request.getAge() != null) patient.setAge(request.getAge());
        if (request.getNumberPhone() != null) patient.setNumberPhone(request.getNumberPhone());
        patient.setUpdatedBy(currentUserId);

        patientRepository.save(patient);

        return ApiDataResponseBuilder.builder()
        .data(mapToResponse(patient))
                .message("Data pasien berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updatePatientStatus(String patientId, PatientStatusUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId).orElse(null);

        if (patient == null || EntityStatus.DELETED.equals(patient.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status pasien tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        patient.setStatus(request.getStatus());
        patient.setUpdatedBy(currentUserId);
        patient.setUpdatedAt(LocalDateTime.now());

        patientRepository.save(patient);

        return ApiDataResponseBuilder.builder()
            .data(mapToResponse(patient))
                .message("Status pasien berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deletePatient(String patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);

        if (patient == null || EntityStatus.DELETED.equals(patient.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data pasien tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        patient.setDeletedAt(LocalDateTime.now());
        patient.setDeletedBy(currentUserId);
        patient.setStatus(EntityStatus.DELETED);

        patientRepository.save(patient);
        log.info("Patient {} berhasil dihapus (soft delete) oleh user {}", patientId, currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(patient))
                .message("Pasien berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private PatientResponse mapToResponse(Patient patient) {
        PatientResponse response = modelMapper.map(patient, PatientResponse.class);
        response.setStatus(patient.getStatus() == null ? null : patient.getStatus().name());
        response.setCreatedAt(patient.getCreatedAt() == null ? null : patient.getCreatedAt().toString());
        response.setUpdatedAt(patient.getUpdatedAt() == null ? null : patient.getUpdatedAt().toString());
        return response;
    }
}

