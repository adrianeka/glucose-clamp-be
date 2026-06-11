package com.tujuhsembilan.glucoseclamp.controller.accessmenu;

import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.AccessMenusService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Access Menu", description = "Access Menu Management APIs")
@RestController
@RequestMapping("/access-menus")
@SecurityRequirement(name = "bearerAuth")
public class AccessMenusController {

    @Autowired
    private AccessMenusService accessMenusService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllAccessMenus(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = accessMenusService.getAllAccessMenus(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAccessMenuById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = accessMenusService.getAccessMenuById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addAccessMenu(@Valid @RequestBody AccessMenuRequest request) {
        ApiDataResponseBuilder result = accessMenusService.addAccessMenu(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAccessMenu(@PathVariable Integer id, @Valid @RequestBody AccessMenuUpdateRequest request) {
        ApiDataResponseBuilder result = accessMenusService.updateAccessMenu(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateAccessMenuStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AccessMenuStatusUpdateRequest request
    ) {
        ApiDataResponseBuilder result = accessMenusService.updateAccessMenuStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAccessMenu(@PathVariable Integer id) {
        ApiDataResponseBuilder result = accessMenusService.deleteAccessMenu(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchAccessMenus(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = accessMenusService.searchAccessMenus(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
