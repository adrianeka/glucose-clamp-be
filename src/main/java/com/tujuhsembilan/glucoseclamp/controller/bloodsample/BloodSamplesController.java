package com.tujuhsembilan.glucoseclamp.controller.bloodsample;

import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.BloodSampleUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.BloodSampleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "BloodSample", description = "Blood sample management APIs")
@RestController
@RequestMapping("/blood-samples")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class BloodSamplesController {

    private final BloodSampleService bloodSampleService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = bloodSampleService.getAllBloodSamples(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable String id) {
        ApiDataResponseBuilder result = bloodSampleService.getBloodSampleById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody BloodSampleRequest request) {
        ApiDataResponseBuilder result = bloodSampleService.addBloodSample(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(
            @PathVariable String id, 
            @Valid @RequestBody BloodSampleRequest request
    ) {
        ApiDataResponseBuilder result = bloodSampleService.updateBloodSample(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable String id, @Valid @RequestBody BloodSampleStatusUpdateRequest request) {
        ApiDataResponseBuilder result = bloodSampleService.updateBloodSampleStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable String id) {
        ApiDataResponseBuilder result = bloodSampleService.deleteBloodSample(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = bloodSampleService.searchBloodSamples(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
