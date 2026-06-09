package com.tujuhsembilan.glucoseclamp.controller.participant;

import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.ParticipantsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tujuhsembilan.glucoseclamp.dto.request.ParticipantStatusUpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Participant", description = "Participant Management APIs")
@RestController
@RequestMapping("/participants")
@SecurityRequirement(name = "bearerAuth")

public class ParticipantsController {

    @Autowired
    private ParticipantsService participantsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllParticipants(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = participantsService.getAllParticipants(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addParticipant(@Valid @RequestBody ParticipantRequest request) {
        ApiDataResponseBuilder result = participantsService.addParticipant(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchParticipantsByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = participantsService.searchParticipantsByKeyword(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{participantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getParticipantById(@PathVariable String participantId) {
        ApiDataResponseBuilder result = participantsService.getParticipantById(participantId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{participantId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateParticipant(
            @PathVariable String participantId,
            @Valid @RequestBody ParticipantUpdateRequest request
    ) {
        ApiDataResponseBuilder result = participantsService.updateParticipant(participantId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{participantId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateParticipantStatus(
            @PathVariable String participantId,
            @Valid @RequestBody ParticipantStatusUpdateRequest request
    ) {
        ApiDataResponseBuilder result = participantsService.updateParticipantStatus(participantId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{participantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteParticipant(@PathVariable String participantId) {
        ApiDataResponseBuilder result = participantsService.deleteParticipant(participantId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
