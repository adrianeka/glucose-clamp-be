package com.tujuhsembilan.glucoseclamp.controller.globalconfiguration;

import com.tujuhsembilan.glucoseclamp.dto.request.GlobalConfigurationRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.GlobalConfigurationService;
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

import java.math.BigInteger;

@Tag(name = "GlobalConfiguration", description = "Global configuration management APIs")
@RestController
@RequestMapping("/global-configuration")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class GlobalConfigurationController {

    private final GlobalConfigurationService globalConfigurationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = globalConfigurationService.getAllGlobalConfigurations(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable BigInteger id) {
        ApiDataResponseBuilder result = globalConfigurationService.getGlobalConfigurationById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody GlobalConfigurationRequest request) {
        ApiDataResponseBuilder result = globalConfigurationService.addGlobalConfiguration(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable BigInteger id, @Valid @RequestBody GlobalConfigurationRequest request) {
        ApiDataResponseBuilder result = globalConfigurationService.updateGlobalConfiguration(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable BigInteger id) {
        ApiDataResponseBuilder result = globalConfigurationService.deleteGlobalConfiguration(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable BigInteger id, @Valid @RequestBody UpdateStatusRequest request) {
        ApiDataResponseBuilder result = globalConfigurationService.updateGlobalConfigurationStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = globalConfigurationService.searchGlobalConfigurations(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
