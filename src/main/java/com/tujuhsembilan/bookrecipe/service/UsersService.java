package com.tujuhsembilan.bookrecipe.service;

import com.tujuhsembilan.bookrecipe.dto.request.LoginRequest;
import com.tujuhsembilan.bookrecipe.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.bookrecipe.dto.response.JwtResponse;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;
import com.tujuhsembilan.bookrecipe.security.jwt.JwtUtils;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import com.tujuhsembilan.bookrecipe.dto.request.RegisterRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.model.Users;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsersService {

    @Autowired
    private UsersRepository userRepository;
    
    @Autowired
    private Validator validator;


    @Transactional
    public MessageResponse register(RegisterRequest request){
        Set<ConstraintViolation<RegisterRequest>> constraintViolations = validator.validate(request);

        if(!constraintViolations.isEmpty()){
            ConstraintViolation<RegisterRequest> firstViolation = constraintViolations.iterator().next();
            String errorMessage = firstViolation.getMessage();
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        log.info("Received registration request: {}", request);

        if(userRepository.existsByUsername(request.getUsername())){
            String errorMessage = "Username telah digunakan oleh user yang telah mendaftar sebelumnya";
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }


        if (!request.getPassword().equals(request.getRetypePassword())) {
            String errorMessage = "Konfirmasi kata sandi tidak sama dengan kata sandi";
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }  


        if(request.getPassword().length() < 6){
            String errorMessage = "Kata sandi tidak boleh kurang dari 6 karakter";
            return new MessageResponse(errorMessage, HttpStatus.BAD_REQUEST.value(), "ERROR");
        }

        Users user = Users.builder()
            .username(request.getUsername())
            .fullname(request.getFullname())
            .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
            .role("User")
            .isDeleted(false)
            .build();

        userRepository.save(user);

        log.info("Received user: {}", user);
        
        String successMessage = "Berhasil menambahkan " + request.getUsername();
        return new MessageResponse(successMessage, HttpStatus.OK.value(), "OK");

    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    final HttpStatus statusOK = HttpStatus.OK;

    public ApiDataResponseBuilder signIn(LoginRequest loginRequest){
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
                .data(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles.get()))
                .message("Auth User Success")
                .statusCode(statusOK.value())
                .status(statusOK)
                .build();   
        } catch (AuthenticationException e){
            return ApiDataResponseBuilder.builder()
                .message("Invalid username or password")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        }catch (Exception e) {
            return ApiDataResponseBuilder.builder()
                .message("Internal server error")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}
