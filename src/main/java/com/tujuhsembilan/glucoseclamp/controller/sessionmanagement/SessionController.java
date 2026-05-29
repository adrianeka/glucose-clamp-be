package com.tujuhsembilan.glucoseclamp.controller.sessionmanagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.glucoseclamp.dto.request.SessionCreateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.SessionManagementService;
import com.tujuhsembilan.glucoseclamp.service.SessionTrackingService;

@Tag(name = "Session", description = "Session APIs")
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionManagementService sessionManagementService;

    @Autowired
    private SessionTrackingService sessionTrackingService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> create(@Valid @RequestBody SessionCreateRequest request) {
        ApiDataResponseBuilder result = sessionManagementService.create(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable Integer sessionId, @Valid @RequestBody SessionUpdateRequest request) {
        ApiDataResponseBuilder result = sessionManagementService.update(sessionId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTimeline(@PathVariable Integer sessionId) {
        ApiDataResponseBuilder result = sessionTrackingService.getTimeline(sessionId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllSessions(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        ApiDataResponseBuilder result = sessionManagementService.getAllSessions(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
