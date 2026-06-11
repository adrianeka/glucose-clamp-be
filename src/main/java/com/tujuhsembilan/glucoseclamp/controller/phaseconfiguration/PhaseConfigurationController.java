package com.tujuhsembilan.glucoseclamp.controller.phaseconfiguration;

import com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.PhaseConfigurationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PhaseConfiguration", description = "Phase configuration management APIs")
@RestController
@RequestMapping("/phase-configuration")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class PhaseConfigurationController {

    private final PhaseConfigurationService phaseConfigurationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = phaseConfigurationService.getAllPhaseConfigurations(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        ApiDataResponseBuilder result = phaseConfigurationService.getPhaseConfigurationById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody PhaseConfigurationRequest request) {
        ApiDataResponseBuilder result = phaseConfigurationService.addPhaseConfiguration(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable Long id, @Valid @RequestBody PhaseConfigurationRequest request) {
        ApiDataResponseBuilder result = phaseConfigurationService.updatePhaseConfiguration(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}/priority", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updatePriority(@PathVariable Long id, @Valid @RequestBody com.tujuhsembilan.glucoseclamp.dto.request.PhaseConfigurationPriorityRequest request) {
        ApiDataResponseBuilder result = phaseConfigurationService.updatePhaseConfigurationPriority(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        ApiDataResponseBuilder result = phaseConfigurationService.deletePhaseConfiguration(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        ApiDataResponseBuilder result = phaseConfigurationService.updatePhaseConfigurationStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = phaseConfigurationService.searchPhaseConfigurations(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
