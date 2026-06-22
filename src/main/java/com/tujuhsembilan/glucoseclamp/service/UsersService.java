package com.tujuhsembilan.glucoseclamp.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lib.i18n.utility.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tujuhsembilan.glucoseclamp.dto.request.LoginRequest;
import com.tujuhsembilan.glucoseclamp.dto.request.RegisterRequest;
import com.tujuhsembilan.glucoseclamp.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.glucoseclamp.dto.response.JwtResponse;
import com.tujuhsembilan.glucoseclamp.dto.response.MessageResponse;
import com.tujuhsembilan.glucoseclamp.model.Role;
import com.tujuhsembilan.glucoseclamp.model.User;
import com.tujuhsembilan.glucoseclamp.model.base.EntityStatus;
import com.tujuhsembilan.glucoseclamp.repository.RoleRepository;
import com.tujuhsembilan.glucoseclamp.repository.UserRepository;
import com.tujuhsembilan.glucoseclamp.security.jwt.JwtUtils;
import com.tujuhsembilan.glucoseclamp.security.service.UserDetailsImplement;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository rolesRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    final HttpStatus statusOK = HttpStatus.OK;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        Set<ConstraintViolation<RegisterRequest>> constraintViolations = validator.validate(request);

        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<RegisterRequest> firstViolation = constraintViolations.iterator().next();
            String errorMessage = firstViolation.getMessage();
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            String errorMessage = messageUtil.get("application.error.password-not-match.user");
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        Optional<Role> role = rolesRepository.findById(request.getRoleId());
        if (role.isEmpty()) {
            return new MessageResponse("Role tidak ditemukan", HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        String normalizedUsername = normalize(request.getUsername());
        Optional<User> existingUsername = userRepository.findByUsernameAndDeletedAtIsNull(normalizedUsername);
        if (existingUsername.isPresent()) {
            String errorMessage = messageUtil.get("application.error.already-exist.user");
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        String normalizedEmail = normalize(request.getEmail());
        Optional<User> existingEmail = userRepository.findByEmailAndDeletedAtIsNull(normalizedEmail);
        if (existingEmail.isPresent()) {
            return new MessageResponse("email sudah digunakan", HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        String encryptedPassword = encoder.encode(request.getPassword());

        User user = User.builder()
                .username(normalizedUsername)
                .name(request.getFullname().trim())
                .email(normalizedEmail) // Mengisi nilai kolom email
                .password(encryptedPassword)
                .role(role.get())
                .positionName(request.getPositionName().trim())
                .build();
        
        user.setStatus(EntityStatus.ACTIVE);
        user.setCreatedBy(null); 
        user.setUpdatedBy(null);

        userRepository.save(user);

        String successMessage = messageUtil.get("application.success.add.user", request.getUsername());
        return new MessageResponse(successMessage, HttpStatus.OK.value(), "OK");
    }

    public ApiDataResponseBuilder signIn(LoginRequest loginRequest) {
        if (Boolean.FALSE.equals(userRepository.existsByUsername(loginRequest.getUsername()))) {
            return ApiDataResponseBuilder.builder()
                    .message(messageUtil.get("application.error.auth.user.not-found"))
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImplement userDetails = (UserDetailsImplement) authentication.getPrincipal();
            Optional<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .findFirst();

            return ApiDataResponseBuilder.builder()
                    .data(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),userDetails.getName(), roles.get()))
                    .message(messageUtil.get("application.success.auth.user"))
                    .statusCode(statusOK.value())
                    .status(statusOK)
                    .build();
        } catch (AuthenticationException e) {
            return ApiDataResponseBuilder.builder()
                    .message(messageUtil.get("application.error.auth.user"))
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        } catch (Exception e) {
            return ApiDataResponseBuilder.builder()
                    .message(messageUtil.get("application.error.internal"))
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}