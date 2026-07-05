package com.tujuhsembilan.glucoseclamp.controller.roleaccess;

import com.tujuhsembilan.glucoseclamp.dto.request.RoleAccessRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.service.RoleAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-management/role-access")
public class RoleAccessController {

    @Autowired
    private RoleAccessService roleAccessService;

    @GetMapping
    public ResponseEntity<ApiDataResponseBuilder> getAllRoleAccess() {
        return ResponseEntity.ok(roleAccessService.getAllRoleAccess());
    }

    @PostMapping
    public ResponseEntity<ApiDataResponseBuilder> createRoleAccess(@RequestBody RoleAccessRequest request) {
        ApiDataResponseBuilder response = roleAccessService.createRoleAccess(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiDataResponseBuilder> updateRoleAccess(
            @PathVariable Integer id,
            @RequestBody RoleAccessRequest request) {
        return ResponseEntity.ok(roleAccessService.updateRoleAccess(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiDataResponseBuilder> deleteRoleAccess(@PathVariable Integer id) {
        return ResponseEntity.ok(roleAccessService.deleteRoleAccess(id));
    }
}