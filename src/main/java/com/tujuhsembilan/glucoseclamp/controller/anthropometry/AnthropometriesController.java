package com.tujuhsembilan.glucoseclamp.controller.anthropometry;

import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AnthropometryUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.AnthropometryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Anthropometry", description = "Anthropometry Management APIs")
@RestController
@RequestMapping("/anthropometries")
@SecurityRequirement(name = "bearerAuth")
public class AnthropometriesController {

    @Autowired
    private AnthropometryService anthropometryService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = anthropometryService.getAllAnthropometries(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = anthropometryService.getAnthropometryById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody AnthropometryRequest request) {
        ApiDataResponseBuilder result = anthropometryService.addAnthropometry(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable Integer id, @Valid @RequestBody AnthropometryUpdateRequest request) {
        ApiDataResponseBuilder result = anthropometryService.updateAnthropometry(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable Integer id, @Valid @RequestBody AnthropometryStatusUpdateRequest request) {
        ApiDataResponseBuilder result = anthropometryService.updateAnthropometryStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        ApiDataResponseBuilder result = anthropometryService.deleteAnthropometry(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = anthropometryService.searchAnthropometries(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
