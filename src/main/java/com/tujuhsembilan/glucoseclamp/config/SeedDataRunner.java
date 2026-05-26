package com.tujuhsembilan.glucoseclamp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tujuhsembilan.glucoseclamp.model.Role;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.repository.RoleRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeedDataRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsernameAndDeletedAtIsNull("superadmin").isPresent()) {
            return;
        }

        Role superadminRole = roleRepository.findByRoleNameAndDeletedAtIsNull("Superadmin")
                .orElseGet(() -> {
                    Role role = Role.builder()
                    .roleName("Superadmin")
                            .build();
                    role.setCreatedBy(1);
                    role.setUpdatedBy(1);
                    return roleRepository.save(role);
                });

        superadminRole.setCreatedBy(superadminRole.getCreatedBy() == null ? 1 : superadminRole.getCreatedBy());
        superadminRole.setUpdatedBy(superadminRole.getUpdatedBy() == null ? 1 : superadminRole.getUpdatedBy());

        User superadminUser = User.builder()
            .name("Superadmin")
            .username("superadmin")
            .email("superadmin@mail.com")
            .password(passwordEncoder.encode("superadmin123"))
            .role(superadminRole)
                .build();
        superadminUser.setCreatedBy(1);
        superadminUser.setUpdatedBy(1);

        userRepository.save(superadminUser);

        // Default Role
        Role defaultRole = roleRepository.findByRoleNameAndDeletedAtIsNull("Operator_Analyzer")
                .orElseGet(() -> {
                    Role role = Role.builder()
                    .roleName("Operator_Analyzer")
                            .build();
                    role.setCreatedBy(1);
                    role.setUpdatedBy(1);
                    return roleRepository.save(role);
                });

        defaultRole.setCreatedBy(defaultRole.getCreatedBy() == null ? 1 : defaultRole.getCreatedBy());
        defaultRole.setUpdatedBy(defaultRole.getUpdatedBy() == null ? 1 : defaultRole.getUpdatedBy());
    }
}