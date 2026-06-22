package com.tujuhsembilan.glucoseclamp.controller.samplingschedule;

import com.tujuhsembilan.glucoseclamp.dto.request.SamplingScheduleRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.SamplingSchedulesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sampling Schedule", description = "Sampling Schedule Management APIs")
@RestController
@RequestMapping("/protocol-management/sampling-schedules")
@SecurityRequirement(name = "bearerAuth")
public class SamplingSchedulesController {

    @Autowired
    private SamplingSchedulesService samplingSchedulesService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSamplingSchedules(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long protocolId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (search != null || startDate != null || endDate != null || protocolId != null) {
            ApiDataResponseBuilder result = samplingSchedulesService.searchSamplingSchedules(protocolId, search, startDate, endDate);
            return ResponseEntity.status(result.getStatus()).body(result);
        }
        ApiDataResponseBuilder result = samplingSchedulesService.getAllSamplingSchedules(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{detailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSamplingScheduleById(@PathVariable Long detailId) {
        ApiDataResponseBuilder result = samplingSchedulesService.getSamplingScheduleById(detailId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(
        path = "/protocol/{protocolId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getSamplingSchedulesByProtocolId(
            @PathVariable Long protocolId
    ) {
        ApiDataResponseBuilder result =
                samplingSchedulesService.getSamplingSchedulesByProtocolId(protocolId);

        return ResponseEntity
                .status(result.getStatus())
                .body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addSamplingSchedule(@Valid @RequestBody SamplingScheduleRequest request) {
        ApiDataResponseBuilder result = samplingSchedulesService.addSamplingSchedule(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{detailId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateSamplingSchedule(@PathVariable Long detailId, @Valid @RequestBody SamplingScheduleRequest request) {
        ApiDataResponseBuilder result = samplingSchedulesService.updateSamplingSchedule(detailId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{detailId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateSamplingScheduleStatus(
            @PathVariable Long detailId,
            @RequestParam String status
    ) {
        ApiDataResponseBuilder result = samplingSchedulesService.updateSamplingScheduleStatus(detailId, status);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{detailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteSamplingSchedule(@PathVariable Long detailId) {
        ApiDataResponseBuilder result = samplingSchedulesService.deleteSamplingSchedule(detailId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
