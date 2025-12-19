package com.tradewise.userservice.controller;

import com.tradewise.userservice.dto.RegisterRequest;
// Import the new User DTO we'll create
import com.tradewise.userservice.dto.response.UserResponse;
import com.tradewise.userservice.model.User; // Keep this import
import com.tradewise.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tradewise.userservice.dto.LoginRequest;
import com.tradewise.userservice.dto.response.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // No more try-catch!
        User registeredUser = authService.registerUser(registerRequest);
        UserResponse response = new UserResponse(
                registeredUser.getId(),
                registeredUser.getEmail(),
                registeredUser.getCreatedAt()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response = authService.loginUser(loginRequest);

        return ResponseEntity.ok(response); // Returns a 200 OK
    }
}