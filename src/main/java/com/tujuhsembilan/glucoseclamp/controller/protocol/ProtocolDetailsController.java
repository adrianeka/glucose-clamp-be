package com.tujuhsembilan.glucoseclamp.controller.protocol;

import com.tujuhsembilan.glucoseclamp.dto.request.ProtocolDetailRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.ProtocolDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Protocol Detail", description = "Protocol Detail Management APIs")
@RestController
@RequestMapping("/protocol-management/protocol-details")
@SecurityRequirement(name = "bearerAuth")
public class ProtocolDetailsController {

    @Autowired
    private ProtocolDetailsService protocolDetailsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProtocolDetails(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String protocolId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (search != null || startDate != null || endDate != null || protocolId != null) {
            ApiDataResponseBuilder result = protocolDetailsService.searchProtocolDetails(protocolId, search, startDate, endDate);
            return ResponseEntity.status(result.getStatus()).body(result);
        }
        ApiDataResponseBuilder result = protocolDetailsService.getAllProtocolDetails(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{detailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProtocolDetailById(@PathVariable String detailId) {
        ApiDataResponseBuilder result = protocolDetailsService.getProtocolDetailById(detailId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addProtocolDetail(@Valid @RequestBody ProtocolDetailRequest request) {
        ApiDataResponseBuilder result = protocolDetailsService.addProtocolDetail(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{detailId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProtocolDetail(@PathVariable String detailId, @Valid @RequestBody ProtocolDetailRequest request) {
        ApiDataResponseBuilder result = protocolDetailsService.updateProtocolDetail(detailId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{detailId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProtocolDetailStatus(
            @PathVariable String detailId,
            @RequestParam String status
    ) {
        ApiDataResponseBuilder result = protocolDetailsService.updateProtocolDetailStatus(detailId, status);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{detailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteProtocolDetail(@PathVariable String detailId) {
        ApiDataResponseBuilder result = protocolDetailsService.deleteProtocolDetail(detailId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
