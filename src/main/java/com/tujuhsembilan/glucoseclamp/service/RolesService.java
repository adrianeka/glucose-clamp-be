package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.RoleRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RoleStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RoleUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.RoleResponse;
import com.tujuhsembilan.glucoseclamp.model.Role;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.RoleRepository;
import com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class RolesService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllRoles(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<RoleResponse> result = roleRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data role")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getRoleById(Integer roleId) {
        Role role = roleRepository.findByIdAndDeletedAtIsNull(roleId).orElse(null);

        if (role == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data role tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(role))
                .message("Berhasil mendapatkan data role")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchRoles(String keyword, int pageNumber, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Keyword pencarian tidak boleh kosong")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<RoleResponse> result = roleRepository.searchByKeyword(keyword.trim(), pageable)
                .map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data role")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addRole(RoleRequest request) {
        if (roleRepository.findByRoleNameAndDeletedAtIsNull(request.getRoleName()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Nama role sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .build();

        LocalDateTime now = LocalDateTime.now();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);
        role.setCreatedBy(currentUserId);
        role.setUpdatedBy(currentUserId);
        role.setStatus(EntityStatus.ACTIVE);

        roleRepository.save(role);
        log.info("Role berhasil ditambahkan: {} oleh user {}", role.getRoleId(), currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(role))
                .message("Role berhasil ditambahkan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateRole(Integer roleId, RoleUpdateRequest request) {
        Role role = roleRepository.findById(roleId).orElse(null);

        if (role == null || EntityStatus.INACTIVE.equals(role.getStatus()) || EntityStatus.DELETED.equals(role.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data role tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (roleRepository.findByRoleNameAndDeletedAtIsNull(request.getRoleName()).isPresent() && 
            !role.getRoleName().equalsIgnoreCase(request.getRoleName())) {
            return ApiDataResponseBuilder.builder()
                    .message("Nama role sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        role.setUpdatedBy(currentUserId);
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(role))
                .message("Data role berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateRoleStatus(Integer roleId, RoleStatusUpdateRequest request) {
        Role role = roleRepository.findById(roleId).orElse(null);

        if (role == null || EntityStatus.DELETED.equals(role.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data role tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status role tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        role.setStatus(request.getStatus());
        role.setUpdatedBy(currentUserId);
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(role))
                .message("Status role berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);

        if (role == null || EntityStatus.DELETED.equals(role.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data role tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        role.setDeletedAt(LocalDateTime.now());
        role.setDeletedBy(currentUserId);
        role.setStatus(EntityStatus.DELETED);

        roleRepository.save(role);
        log.info("Role {} berhasil dihapus (soft delete) oleh user {}", roleId, currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(role))
                .message("Role berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private RoleResponse mapToResponse(Role role) {
        RoleResponse response = modelMapper.map(role, RoleResponse.class);
        response.setStatus(role.getStatus() == null ? null : role.getStatus().name());
        response.setCreatedAt(role.getCreatedAt() == null ? null : role.getCreatedAt().toString());
        response.setUpdatedAt(role.getUpdatedAt() == null ? null : role.getUpdatedAt().toString());
        return response;
    }
}
