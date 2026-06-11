package com.tujuhsembilan.glucoseclamp.service;

import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuStatusUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.AccessMenuUpdateRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.AccessMenuResponse;
import com.tujuhsembilan.glucoseclamp.model.AccessMenu;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.AccessMenuRepository;
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

@Slf4j
@Service
public class AccessMenusService {

    @Autowired
    private AccessMenuRepository accessMenuRepository;

    @Autowired
    private ModelMapper modelMapper;

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
        return userDetails.getId();
    }

    public ApiDataResponseBuilder getAllAccessMenus(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<AccessMenuResponse> result = accessMenuRepository.findAllActive(pageable).map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data access menu")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder getAccessMenuById(Integer menuId) {
        AccessMenu menu = accessMenuRepository.findByIdAndDeletedAtIsNull(menuId).orElse(null);

        if (menu == null) {
            return ApiDataResponseBuilder.builder()
                    .message("Data access menu tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(menu))
                .message("Berhasil mendapatkan data access menu")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    public ApiDataResponseBuilder searchAccessMenus(String keyword, int pageNumber, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiDataResponseBuilder.builder()
                    .message("Keyword pencarian tidak boleh kosong")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<AccessMenuResponse> result = accessMenuRepository.searchByKeyword(keyword.trim(), pageable)
                .map(this::mapToResponse);

        return ApiDataResponseBuilder.builder()
                .data(result)
                .message("Berhasil mendapatkan data access menu")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder addAccessMenu(AccessMenuRequest request) {
        if (accessMenuRepository.findByMenuNameAndDeletedAtIsNull(request.getMenuName()).isPresent()) {
            return ApiDataResponseBuilder.builder()
                    .message("Nama menu sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        AccessMenu menu = AccessMenu.builder()
                .menuName(request.getMenuName())
                .build();

        LocalDateTime now = LocalDateTime.now();
        menu.setCreatedAt(now);
        menu.setUpdatedAt(now);
        menu.setCreatedBy(currentUserId);
        menu.setUpdatedBy(currentUserId);
        menu.setStatus(EntityStatus.ACTIVE);

        accessMenuRepository.save(menu);
        log.info("Access menu berhasil ditambahkan: {} oleh user {}", menu.getMenuId(), currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(menu))
                .message("Access menu berhasil ditambahkan")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateAccessMenu(Integer menuId, AccessMenuUpdateRequest request) {
        AccessMenu menu = accessMenuRepository.findById(menuId).orElse(null);

        if (menu == null || EntityStatus.INACTIVE.equals(menu.getStatus()) || EntityStatus.DELETED.equals(menu.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data access menu tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (accessMenuRepository.findByMenuNameAndDeletedAtIsNull(request.getMenuName()).isPresent() && 
            !menu.getMenuName().equalsIgnoreCase(request.getMenuName())) {
            return ApiDataResponseBuilder.builder()
                    .message("Nama menu sudah digunakan")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        if (request.getMenuName() != null) {
            menu.setMenuName(request.getMenuName());
        }
        menu.setUpdatedBy(currentUserId);
        menu.setUpdatedAt(LocalDateTime.now());

        accessMenuRepository.save(menu);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(menu))
                .message("Data access menu berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder updateAccessMenuStatus(Integer menuId, AccessMenuStatusUpdateRequest request) {
        AccessMenu menu = accessMenuRepository.findById(menuId).orElse(null);

        if (menu == null || EntityStatus.DELETED.equals(menu.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data access menu tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        if (EntityStatus.DELETED.equals(request.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Status access menu tidak valid")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        menu.setStatus(request.getStatus());
        menu.setUpdatedBy(currentUserId);
        menu.setUpdatedAt(LocalDateTime.now());

        accessMenuRepository.save(menu);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(menu))
                .message("Status access menu berhasil diupdate")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    @Transactional
    public ApiDataResponseBuilder deleteAccessMenu(Integer menuId) {
        AccessMenu menu = accessMenuRepository.findById(menuId).orElse(null);

        if (menu == null || EntityStatus.DELETED.equals(menu.getStatus())) {
            return ApiDataResponseBuilder.builder()
                    .message("Data access menu tidak ditemukan")
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        Integer currentUserId = getCurrentUserId();

        menu.setDeletedAt(LocalDateTime.now());
        menu.setDeletedBy(currentUserId);
        menu.setStatus(EntityStatus.DELETED);

        accessMenuRepository.save(menu);
        log.info("Access menu {} berhasil dihapus (soft delete) oleh user {}", menuId, currentUserId);

        return ApiDataResponseBuilder.builder()
                .data(mapToResponse(menu))
                .message("Access menu berhasil dihapus")
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .build();
    }

    private AccessMenuResponse mapToResponse(AccessMenu menu) {
        AccessMenuResponse response = modelMapper.map(menu, AccessMenuResponse.class);
        response.setStatus(menu.getStatus() == null ? null : menu.getStatus().name());
        response.setCreatedAt(menu.getCreatedAt() == null ? null : menu.getCreatedAt().toString());
        response.setUpdatedAt(menu.getUpdatedAt() == null ? null : menu.getUpdatedAt().toString());
        return response;
    }
}
