package com.example.smartwaste.controller;

import com.example.smartwaste.dto.*;
import com.example.smartwaste.model.Admin;
import com.example.smartwaste.repository.AdminRepository;
import com.example.smartwaste.security.JwtUtil;
import com.example.smartwaste.service.CustomUserDetailsService; // Added if not present
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /** Login – returns a JWT token */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());

            authenticationManager.authenticate(authToken);

            // Retrieve user (to include any extra claims later)
            Admin admin = adminRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).build();
        }
    }

    /** Logout – stateless JWT, so just inform the client to discard the token */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // In a real system you might keep a token blacklist; omitted for brevity.
        return ResponseEntity.ok().build();
    }
}
