package com.tujuhsembilan.glucoseclamp.controller.protocol;

import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.ProtocolsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Protocol", description = "Protocol Management APIs")
@RestController
@RequestMapping("/protocol-management/protocols")
@SecurityRequirement(name = "bearerAuth")
public class ProtocolsController {

    @Autowired
    private ProtocolsService protocolsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProtocols(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (search != null || startDate != null || endDate != null) {
            ApiDataResponseBuilder result = protocolsService.searchProtocols(search, startDate, endDate);
            return ResponseEntity.status(result.getStatus()).body(result);
        }
        ApiDataResponseBuilder result = protocolsService.getAllProtocols(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{protocolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProtocolById(@PathVariable Long protocolId) {
        ApiDataResponseBuilder result = protocolsService.getProtocolById(protocolId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/dropdown", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProtocolsDropdown() {
        ApiDataResponseBuilder result = protocolsService.getProtocolsDropdown();
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addProtocol(@Valid @RequestBody ProtocolRequest request) {
        ApiDataResponseBuilder result = protocolsService.addProtocol(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{protocolId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProtocol(@PathVariable Long protocolId, @Valid @RequestBody ProtocolRequest request) {
        ApiDataResponseBuilder result = protocolsService.updateProtocol(protocolId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{protocolId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProtocolStatus(
            @PathVariable Long protocolId,
            @RequestParam String status
    ) {
        ApiDataResponseBuilder result = protocolsService.updateProtocolStatus(protocolId, status);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{protocolId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteProtocol(@PathVariable Long protocolId) {
        ApiDataResponseBuilder result = protocolsService.deleteProtocol(protocolId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
