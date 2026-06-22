package com.tujuhsembilan.glucoseclamp.controller.infusionmonitoring;

import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.InfusionMonitoringUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.InfusionMonitoringService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "InfusionMonitoring", description = "Infusion monitoring management APIs")
@RestController
@RequestMapping("/infusion-monitoring")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class InfusionMonitoringsController {

    private final InfusionMonitoringService infusionMonitoringService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = infusionMonitoringService.getAllInfusionMonitorings(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable String id) {
        ApiDataResponseBuilder result = infusionMonitoringService.getInfusionMonitoringById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody InfusionMonitoringRequest request) {
        ApiDataResponseBuilder result = infusionMonitoringService.addInfusionMonitoring(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable String id, @Valid @RequestBody InfusionMonitoringUpdateRequest request) {
        ApiDataResponseBuilder result = infusionMonitoringService.updateInfusionMonitoring(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable String id, @Valid @RequestBody InfusionMonitoringStatusUpdateRequest request) {
        ApiDataResponseBuilder result = infusionMonitoringService.updateInfusionMonitoringStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable String id) {
        ApiDataResponseBuilder result = infusionMonitoringService.deleteInfusionMonitoring(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = infusionMonitoringService.searchInfusionMonitorings(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
    @GetMapping("/recommendation")
    public ResponseEntity<ApiDataResponseBuilder> getRecommendation(@RequestParam Long sessionId) {
        ApiDataResponseBuilder response = infusionMonitoringService.getLatestGirRecommendation(sessionId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
