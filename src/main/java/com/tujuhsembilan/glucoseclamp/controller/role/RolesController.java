package com.tujuhsembilan.glucoseclamp.controller.role;

import com.tujuhsembilan.glucoseclamp.dto.request.RoleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RoleStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RoleUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.RolesService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role", description = "Role Management APIs")
@RestController
@RequestMapping("/roles")
@SecurityRequirement(name = "bearerAuth")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllRoles(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = rolesService.getAllRoles(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getRoleById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = rolesService.getRoleById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addRole(@Valid @RequestBody RoleRequest request) {
        ApiDataResponseBuilder result = rolesService.addRole(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateRole(@PathVariable Integer id, @Valid @RequestBody RoleUpdateRequest request) {
        ApiDataResponseBuilder result = rolesService.updateRole(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PatchMapping(path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateRoleStatus(
            @PathVariable Integer id,
            @Valid @RequestBody RoleStatusUpdateRequest request
    ) {
        ApiDataResponseBuilder result = rolesService.updateRoleStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        ApiDataResponseBuilder result = rolesService.deleteRole(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchRoles(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = rolesService.searchRoles(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
