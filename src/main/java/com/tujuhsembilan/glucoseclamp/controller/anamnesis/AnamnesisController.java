package com.tujuhsembilan.glucoseclamp.controller.anamnesis;

import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnamnesisUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.AnamnesisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Anamnesis", description = "Anamnesis Management APIs")
@RestController
@RequestMapping("/anamneses")
@SecurityRequirement(name = "bearerAuth")
public class AnamnesisController {

    @Autowired
    private AnamnesisService anamnesisService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = anamnesisService.getAllAnamneses(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = anamnesisService.getAnamnesisById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody AnamnesisRequest request) {
        ApiDataResponseBuilder result = anamnesisService.addAnamnesis(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable Integer id, @Valid @RequestBody AnamnesisUpdateRequest request) {
        ApiDataResponseBuilder result = anamnesisService.updateAnamnesis(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable Integer id, @Valid @RequestBody AnamnesisStatusUpdateRequest request) {
        ApiDataResponseBuilder result = anamnesisService.updateAnamnesisStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        ApiDataResponseBuilder result = anamnesisService.deleteAnamnesis(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = anamnesisService.searchAnamneses(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
