package com.tradewise.userservice.service;

import com.tradewise.userservice.dto.RegisterRequest;
import com.tradewise.userservice.model.User;
import com.tradewise.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tradewise.userservice.dto.LoginRequest;
import com.tradewise.userservice.dto.response.LoginResponse;
import com.tradewise.userservice.dto.response.UserResponse;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Spring injects the beans we need
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) { // <-- ADD jwtUtil
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; // <-- ADD THIS
    }

    @Transactional // Ensures this method runs as a single database transaction
    public User registerUser(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already taken!");
        }

        // 2. Create new user's account
        User user = new User();
        user.setEmail(request.getEmail());

        // 3. Hash the password before saving
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // 4. Save to the database
        return userRepository.save(user);
    }

    public LoginResponse loginUser(LoginRequest request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password")); // Generic message

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password"); // Generic message
        }

        // 3. Generate JWT
        String token = jwtUtil.generateToken(user);

        // 4. Create UserResponse (the one you built)
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );

        // 5. Return the full LoginResponse
        return new LoginResponse(token, userResponse);
    }
}