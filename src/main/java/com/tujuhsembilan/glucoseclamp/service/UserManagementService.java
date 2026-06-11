package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.UserManagementRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.UpdateStatusRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.UserManagementResponse;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.Role;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import com.tujuhsembilan.glucoseclamp.repository.RoleRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        try {
            var principal = authentication.getPrincipal();
            var userDetails = (com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement) principal;
            return userDetails.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    public ApiDataResponseBuilder getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<UserManagementResponse> result = userRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data user")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getUserById(Integer id) {
        Optional<User> user = userRepository.findByIdAndDeletedAtIsNull(id);
        if (user.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data user tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(user.get()))
                .message("Berhasil mendapatkan data user")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addUser(UserManagementRequest request) {
        String normalizedUsername = normalize(request.getUsername());
        Optional<User> existingUsername = userRepository.findByUsernameAndDeletedAtIsNull(normalizedUsername);
        if (existingUsername.isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("username sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        String normalizedEmail = normalize(request.getEmail());
        Optional<User> existingEmail = userRepository.findByEmailAndDeletedAtIsNull(normalizedEmail);
        if (existingEmail.isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("email sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Role> role = roleRepository.findById(request.getRoleId());
        if (role.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Role tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .role(role.get())
                .positionName(request.getPositionName().trim())
                .name(request.getName().trim())
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(encryptedPassword)
                .build();
        user.setStatus(EntityStatus.ACTIVE);
        user.setCreatedBy(currentUserId);
        user.setUpdatedBy(currentUserId);

        userRepository.save(user);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(user))
                .message("User berhasil ditambahkan")
                .statusCode(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateUser(Integer id, UserManagementRequest request) {
        Optional<User> existingUser = userRepository.findByIdAndDeletedAtIsNull(id);
        if (existingUser.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data user tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        User user = existingUser.get();
        String normalizedUsername = normalize(request.getUsername());
        Optional<User> duplicateUsername = userRepository.findByUsernameAndDeletedAtIsNull(normalizedUsername);
        if (duplicateUsername.isPresent() && !duplicateUsername.get().getUserId().equals(user.getUserId())) {
            return ApiDataResponseBuilder.builder()
                    .message("username sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        String normalizedEmail = normalize(request.getEmail());
        Optional<User> duplicateEmail = userRepository.findByEmailAndDeletedAtIsNull(normalizedEmail);
        if (duplicateEmail.isPresent() && !duplicateEmail.get().getUserId().equals(user.getUserId())) {
            return ApiDataResponseBuilder.builder()
                    .message("email sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Optional<Role> role = roleRepository.findById(request.getRoleId());
        if (role.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Role tidak ditemukan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        user.setRole(role.get());
        user.setPositionName(request.getPositionName().trim());
        user.setName(request.getName().trim());
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            String encryptedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encryptedPassword);
        }
        
        user.setUpdatedBy(getCurrentUserId());
        userRepository.save(user);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(user))
                .message("User berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteUser(Integer id) {
        Optional<User> existingUser = userRepository.findByIdAndDeletedAtIsNull(id);
        if (existingUser.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data user tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        User user = existingUser.get();
        Integer currentUserId = getCurrentUserId();
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(currentUserId);
        user.setStatus(EntityStatus.DELETED);
        user.setUpdatedBy(currentUserId);
        userRepository.save(user);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(user))
                .message("User berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateUserStatus(Integer id, UpdateStatusRequest request) {
        Optional<User> existingUser = userRepository.findByIdAndDeletedAtIsNull(id);
        if (existingUser.isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Data user tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        User user = existingUser.get();
        Integer currentUserId = getCurrentUserId();
        user.setStatus(request.getStatus());
        user.setUpdatedBy(currentUserId);
        user.setUpdatedAt(LocalDateTime.now());
        if (EntityStatus.DELETED.equals(request.getStatus())) {
            user.setDeletedAt(LocalDateTime.now());
            user.setDeletedBy(currentUserId);
        }
        userRepository.save(user);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(user))
                .message("Status user berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchUsers(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(Math.max(0, pageNumber - 1), pageSize);
        Page<UserManagementResponse> result;
        if (keyword == null || keyword.isBlank()) {
            result = userRepository.findAllActive(pageable).map(this::mapToResponse);
        } else {
            result = userRepository.searchByKeyword(keyword.trim(), pageable).map(this::mapToResponse);
        }

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mencari user")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private UserManagementResponse mapToResponse(User user) {
        UserManagementResponse response = modelMapper.map(user, UserManagementResponse.class);
        response.setStatus(user.getStatus() == null ? null : user.getStatus().name());
        if (user.getRole() != null) {
            response.setRoleId(user.getRole().getRoleId());
            response.setRoleName(user.getRole().getRoleName());
        }
        return response;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
