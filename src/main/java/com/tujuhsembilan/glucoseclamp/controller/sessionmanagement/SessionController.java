package com.tujuhsembilan.glucoseclamp.controller.sessionmanagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.glucoseclamp.dto.request.SessionCreateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionCompleteRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.SessionUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.MessageResponse;
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


    @PutMapping(path = "/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable Long sessionId, @Valid @RequestBody SessionUpdateRequest request) {
        ApiDataResponseBuilder result = sessionManagementService.update(sessionId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{sessionId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable Long sessionId, @Valid @RequestBody com.tujuhsembilan.glucoseclamp.dto.request.SessionStatusUpdateRequest request) {
        ApiDataResponseBuilder result = sessionManagementService.updateSessionStatus(sessionId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteSession(@PathVariable Long sessionId) {
        ApiDataResponseBuilder result = sessionManagementService.deleteSession(sessionId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTimeline(@PathVariable Long sessionId) {
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

    @PostMapping(path = "/{sessionId}/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse startSession(@PathVariable Long sessionId) {
        return sessionManagementService.startSession(sessionId);
    }

    @PostMapping(path = "/{sessionId}/complete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> completeSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody SessionCompleteRequest request) {
        ApiDataResponseBuilder result = sessionManagementService.complete(sessionId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
