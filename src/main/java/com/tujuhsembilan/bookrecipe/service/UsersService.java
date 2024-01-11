package com.tujuhsembilan.bookrecipe.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tujuhsembilan.bookrecipe.dto.request.LoginRequest;
import com.tujuhsembilan.bookrecipe.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.bookrecipe.dto.response.JwtResponse;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;
import com.tujuhsembilan.bookrecipe.security.jwt.JwtUtils;
import com.tujuhsembilan.bookrecipe.security.service.UserDetailsImplement;

@Service
public class UsersService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository userRepository;

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
        } catch (Exception e) {
            return ApiDataResponseBuilder.builder()
                .message("Invalid username or password")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        }
    }
}
