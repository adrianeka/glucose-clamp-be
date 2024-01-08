package com.tujuhsembilan.bookrecipe.controller.usermanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tujuhsembilan.bookrecipe.dto.request.RegisterRequest;
import com.tujuhsembilan.bookrecipe.dto.response.MessageResponse;
import com.tujuhsembilan.bookrecipe.dto.response.RegisterResponse;
import com.tujuhsembilan.bookrecipe.service.UsersService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping(
        path = "/user-management/users/sign-up",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )

    public MessageResponse register(@RequestBody RegisterRequest request){
        return usersService.register(request);
    }
}
