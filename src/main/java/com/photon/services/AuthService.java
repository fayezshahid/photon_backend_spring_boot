package com.photon.services;

import com.photon.dtos.*;
import com.photon.entities.User;
import com.photon.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        // Check if user already exists
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = createUserFromRequest(req);
        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtService.generateToken(savedUser.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest req) {
        // Find user by email
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate token
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    private User createUserFromRequest(RegisterRequest req) {
        User user = new User();
        user.setFirstName(req.firstName());
        user.setLastName(req.lastName());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        return user;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername(); // Email is used as username

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            return user.getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
