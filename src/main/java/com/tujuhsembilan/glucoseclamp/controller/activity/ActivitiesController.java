package com.tujuhsembilan.glucoseclamp.controller.activity;

import com.tujuhsembilan.glucoseclamp.dto.request.ActivityRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ActivityStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.ActivityUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.ActivityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Activity", description = "Activity management APIs")
@RestController
@RequestMapping("/activities")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ActivitiesController {

    private final ActivityService activityService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllActivities(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        ApiDataResponseBuilder result = activityService.getAllActivities(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{activityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(@PathVariable String activityId) {
        ApiDataResponseBuilder result = activityService.getActivityById(activityId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/session/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getBySession(@PathVariable Integer sessionId) {
        ApiDataResponseBuilder result = activityService.getActivitiesBySession(sessionId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> add(@Valid @RequestBody ActivityRequest request) {
        ApiDataResponseBuilder result = activityService.addActivity(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{activityId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(@PathVariable String activityId, @Valid @RequestBody ActivityUpdateRequest request) {
        ApiDataResponseBuilder result = activityService.updateActivity(activityId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{activityId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateStatus(@PathVariable String activityId, @Valid @RequestBody ActivityStatusUpdateRequest request) {
        ApiDataResponseBuilder result = activityService.updateActivityStatus(activityId, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{activityId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable String activityId) {
        ApiDataResponseBuilder result = activityService.deleteActivity(activityId);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}