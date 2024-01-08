package com.tujuhsembilan.bookrecipe.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.tujuhsembilan.bookrecipe.dto.request.RegisterRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.model.Users;
import com.tujuhsembilan.bookrecipe.repository.UsersRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
        
        String successMessage = "User " + request.getUsername() + " registered successfully!";
        return new MessageResponse(successMessage, HttpStatus.OK.value(), "OK");

    }
}
