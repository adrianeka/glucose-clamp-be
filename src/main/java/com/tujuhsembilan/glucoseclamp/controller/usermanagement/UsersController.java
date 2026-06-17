package com.tujuhsembilan.glucoseclamp.controller.usermanagement;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.glucoseclamp.dto.request.LoginRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RegisterRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UserManagementRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UserManagementRequestEdit;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.MessageResponse;
import com.tujuhsembilan.glucoseclamp.service.UsersService;
import com.tujuhsembilan.glucoseclamp.service.UserManagementService;

@Tag(name = "User", description = "User Management APIs")
@RestController
@RequestMapping("/user-management")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserManagementService userManagementService;

    @PostMapping(path = "/users/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        MessageResponse response = usersService.register(request);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/users/sign-in")
    public ResponseEntity<Object> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        ApiDataResponseBuilder result = usersService.signIn(loginRequest);

        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = userManagementService.getAllUsers(pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        ApiDataResponseBuilder result = userManagementService.getUserById(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserManagementRequest request) {
        ApiDataResponseBuilder result = userManagementService.addUser(request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(path = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @Valid @RequestBody UserManagementRequestEdit request) {
        ApiDataResponseBuilder result = userManagementService.updateUser(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping(path = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        ApiDataResponseBuilder result = userManagementService.deleteUser(id);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(path = "/users/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateUserStatus(@PathVariable Integer id, @Valid @RequestBody UpdateStatusRequest request) {
        ApiDataResponseBuilder result = userManagementService.updateUserStatus(id, request);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    // @SecurityRequirement(name = "bearerAuth")
    @GetMapping(path = "/users/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        ApiDataResponseBuilder result = userManagementService.searchUsers(keyword, pageNumber, pageSize);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}

