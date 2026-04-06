package com.omkar.Finance_Dashboard.controller;

import com.omkar.Finance_Dashboard.dto.AuthRequest;
import com.omkar.Finance_Dashboard.dto.AuthResponse;
import com.omkar.Finance_Dashboard.dto.RegisterRequest;
import com.omkar.Finance_Dashboard.model.AccountStatus;
import com.omkar.Finance_Dashboard.model.RoleName;
import com.omkar.Finance_Dashboard.model.User;
import com.omkar.Finance_Dashboard.repository.UserRepository;
import com.omkar.Finance_Dashboard.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : RoleName.VIEWER)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, "127.0.0.1");

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, "127.0.0.1");

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }
}
