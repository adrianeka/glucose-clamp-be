package com.tujuhsembilan.bookrecipe.controller.usermanagement;

import com.tujuhsembilan.bookrecipe.dto.request.LoginRequest;
import com.tujuhsembilan.bookrecipe.dto.request.RegisterRequest;
import com.tujuhsembilan.bookrecipe.dto.response.ApiDataResponseBuilder;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.service.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User Management APIs")
@RestController
@RequestMapping("/user-management")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping(path = "/users/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public MessageResponse register(@RequestBody RegisterRequest request) {
        return usersService.register(request);
    }

    @PostMapping("/users/sign-in")
    public ResponseEntity<Object> signIn(@Valid @RequestBody LoginRequest loginRequest) {
        ApiDataResponseBuilder result = usersService.signIn(loginRequest);

        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
