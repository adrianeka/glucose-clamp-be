package com.tujuhsembilan.glucoseclamp.controller.device;

import com.tujuhsembilan.glucoseclamp.dto.request.DeviceRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.DeviceStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.DeviceUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.DevicesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Device", description = "Device Management APIs")
@RestController
@RequestMapping("/devices")
@SecurityRequirement(name = "bearerAuth")
public class DevicesController {

    @Autowired
    private DevicesService devicesService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllDevices(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = devicesService.getAllDevices(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDeviceById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = devicesService.getDeviceById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addDevice(@Valid @RequestBody DeviceRequest request) {
        ApiDataResponseBuilder result = devicesService.addDevice(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateDevice(@PathVariable Integer id, @Valid @RequestBody DeviceUpdateRequest request) {
        ApiDataResponseBuilder result = devicesService.updateDevice(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateDeviceStatus(
            @PathVariable Integer id,
            @Valid @RequestBody DeviceStatusUpdateRequest request
    ) {
        ApiDataResponseBuilder result = devicesService.updateDeviceStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDevice(@PathVariable Integer id) {
        ApiDataResponseBuilder result = devicesService.deleteDevice(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchDevices(@RequestParam(required = false) String keyword) {
        ApiDataResponseBuilder result = devicesService.searchDevices(keyword);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
