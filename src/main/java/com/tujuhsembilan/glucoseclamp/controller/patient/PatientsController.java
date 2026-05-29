package com.tujuhsembilan.glucoseclamp.controller.patient;

import com.tujuhsembilan.glucoseclamp.dto.request.PatientRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.PatientsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tujuhsembilan.glucoseclamp.dto.request.PatientStatusUpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

@Tag(name = "Patient", description = "Patient Management APIs")
@RestController
@RequestMapping("/patients")
@SecurityRequirement(name = "bearerAuth")

public class PatientsController {

    @Autowired
    private PatientsService patientsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllPatients(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = patientsService.getAllPatients(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addPatient(@Valid @RequestBody PatientRequest request) {
        ApiDataResponseBuilder result = patientsService.addPatient(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchPatientsByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = patientsService.searchPatientsByKeyword(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPatientById(@PathVariable String patientId) {
        ApiDataResponseBuilder result = patientsService.getPatientById(patientId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{patientId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePatient(
            @PathVariable String patientId,
            @Valid @RequestBody PatientUpdateRequest request
    ) {
        ApiDataResponseBuilder result = patientsService.updatePatient(patientId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{patientId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePatientStatus(
            @PathVariable String patientId,
            @Valid @RequestBody PatientStatusUpdateRequest request
    ) {
        ApiDataResponseBuilder result = patientsService.updatePatientStatus(patientId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deletePatient(@PathVariable String patientId) {
        ApiDataResponseBuilder result = patientsService.deletePatient(patientId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
