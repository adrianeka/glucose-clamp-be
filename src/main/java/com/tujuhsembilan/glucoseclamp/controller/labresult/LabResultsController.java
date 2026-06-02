package com.tujuhsembilan.glucoseclamp.controller.labresult;

import com.tujuhsembilan.glucoseclamp.dto.request.LabResultRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.LabResultsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lab Result", description = "Lab Result Management APIs")
@RestController
@RequestMapping("/lab-results")
@SecurityRequirement(name = "bearerAuth")
public class LabResultsController {

    @Autowired
    private LabResultsService labResultsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLabResults(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (search != null || startDate != null || endDate != null) {
            ApiDataResponseBuilder result = labResultsService.searchLabResults(search, startDate, endDate);
            return ResponseEntity.status(result.getStatus()).body(result);
        }
        ApiDataResponseBuilder result = labResultsService.getAllLabResults(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{labResultId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLabResultById(@PathVariable String labResultId) {
        ApiDataResponseBuilder result = labResultsService.getLabResultById(labResultId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addLabResult(@Valid @RequestBody LabResultRequest request) {
        ApiDataResponseBuilder result = labResultsService.addLabResult(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{labResultId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateLabResult(@PathVariable String labResultId, @Valid @RequestBody LabResultRequest request) {
        ApiDataResponseBuilder result = labResultsService.updateLabResult(labResultId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{labResultId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateLabResultStatus(
            @PathVariable String labResultId,
            @RequestParam String status
    ) {
        ApiDataResponseBuilder result = labResultsService.updateLabResultStatus(labResultId, status);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{labResultId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteLabResult(@PathVariable String labResultId) {
        ApiDataResponseBuilder result = labResultsService.deleteLabResult(labResultId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
