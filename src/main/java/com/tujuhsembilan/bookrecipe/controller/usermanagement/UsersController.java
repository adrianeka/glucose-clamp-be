package com.tujuhsembilan.bookrecipe.controller.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.bookrecipe.dto.request.LoginRequest;
import com.tujuhsembilan.bookrecipe.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.bookrecipe.service.UsersService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user-management")
public class UsersController {
    @Autowired
    UsersService usersService;

    @PostMapping("/users/signin")
    public ResponseEntity<Object> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        ApiDataResponseBuilder result = usersService.signIn(loginRequest);

        return ResponseEntity.ok(result);
    }
}
