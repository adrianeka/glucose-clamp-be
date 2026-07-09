package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.RoleAccessRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.PermissionResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.RoleAccessResponse;
import com.tujuhsembilan.glucoseclamp.model.AccessMenu;
import com.tujuhsembilan.glucoseclamp.model.Role;
import com.tujuhsembilan.glucoseclamp.model.RoleAccess;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.repository.AccessMenuRepository;
import com.tujuhsembilan.glucoseclamp.repository.RoleAccessRepository;
import com.tujuhsembilan.glucoseclamp.repository.RoleRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleAccessService {

    @Autowired
    private RoleAccessRepository roleAccessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccessMenuRepository accessMenuRepository;

    public ApiDataResponseBuilder getMyPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByUsernameAndDeletedAtIsNull(currentUsername)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getRole() == null) {
            throw new RuntimeException("User tidak memiliki Role yang terasosiasi");
        }

        Integer roleId = user.getRole().getRoleId();
        List<RoleAccess> roleAccessList = roleAccessRepository.findByRoleIdAndDeletedAtIsNull(roleId);

        List<PermissionResponse> permissionResponses = roleAccessList.stream()
                .map(ra -> PermissionResponse.builder()
                        .menuId(ra.getAccessMenu().getMenuId())
                        .menuName(ra.getAccessMenu().getMenuName())
                        .canView(ra.getCanView())
                        .canAdd(ra.getCanAdd())
                        .canEdit(ra.getCanEdit())
                        .canDelete(ra.getCanDelete())
                        .build())
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.OK)
                .message("Get User Permissions Success")
                .data(permissionResponses)
                .build();
    }

    public ApiDataResponseBuilder getAllRoleAccess() {
        List<RoleAccess> list = roleAccessRepository.findAllActive();
        
        List<RoleAccessResponse> responses = list.stream()
                .filter(ra -> ra.getRole() != null && !"Superadmin".equals(ra.getRole().getRoleName()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.OK)
                .message("Get All Role Access Success")
                .data(responses)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder createRoleAccess(RoleAccessRequest request) {
        roleAccessRepository.findByRoleIdAndMenuIdAndDeletedAtIsNull(request.getRoleId(), request.getMenuId())
                .ifPresent(existing -> {
                    throw new RuntimeException("Akses untuk Role dan Menu ini sudah ada");
                });

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role tidak ditemukan"));
        AccessMenu menu = accessMenuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new RuntimeException("Menu tidak ditemukan"));

        RoleAccess roleAccess = RoleAccess.builder()
                .role(role)
                .accessMenu(menu)
                .canView(request.getCanView() != null ? request.getCanView() : false)
                .canAdd(request.getCanAdd() != null ? request.getCanAdd() : false)
                .canEdit(request.getCanEdit() != null ? request.getCanEdit() : false)
                .canDelete(request.getCanDelete() != null ? request.getCanDelete() : false)
                .build();

        RoleAccess saved = roleAccessRepository.save(roleAccess);

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.CREATED)
                .message("Create Role Access Success")
                .data(convertToResponse(saved))
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateRoleAccess(Integer id, RoleAccessRequest request) {
        RoleAccess roleAccess = roleAccessRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Role Access tidak ditemukan"));

        if (request.getCanView() != null) roleAccess.setCanView(request.getCanView());
        if (request.getCanAdd() != null) roleAccess.setCanAdd(request.getCanAdd());
        if (request.getCanEdit() != null) roleAccess.setCanEdit(request.getCanEdit());
        if (request.getCanDelete() != null) roleAccess.setCanDelete(request.getCanDelete());

        RoleAccess updated = roleAccessRepository.save(roleAccess);

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.OK)
                .message("Update Role Access Success")
                .data(convertToResponse(updated))
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteRoleAccess(Integer id) {
        RoleAccess roleAccess = roleAccessRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Role Access tidak ditemukan"));

        roleAccess.setDeletedAt(LocalDateTime.now()); // Set waktu penghapusan
        roleAccessRepository.save(roleAccess);

        return ApiDataResponseBuilder.builder()
                .status(HttpStatus.OK)
                .message("Delete Role Access Success")
                .data(null)
                .build();
    }

    // Helper method untuk konversi model ke DTO Response
    private RoleAccessResponse convertToResponse(RoleAccess ra) {
        return RoleAccessResponse.builder()
                .roleAccessId(ra.getRoleAccessId())
                .roleId(ra.getRole().getRoleId())
                .roleName(ra.getRole().getRoleName())
                .menuId(ra.getAccessMenu().getMenuId())
                .menuName(ra.getAccessMenu().getMenuName())
                .canView(ra.getCanView())
                .canAdd(ra.getCanAdd())
                .canEdit(ra.getCanEdit())
                .canDelete(ra.getCanDelete())
                .build();
    }
}