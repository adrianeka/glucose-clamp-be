package com.tujuhsembilan.glucoseclamp.security.service;

import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public UserDetailsImplement getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImplement)) {
            throw new IllegalStateException("User belum login atau token tidak valid");
        }

        return (UserDetailsImplement) authentication.getPrincipal();
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public User getCurrentUserEntity() {
        Integer currentUserId = getCurrentUserId();
        return userRepository.findByIdAndDeletedAtIsNull(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Data user login tidak ditemukan"));
    }
}