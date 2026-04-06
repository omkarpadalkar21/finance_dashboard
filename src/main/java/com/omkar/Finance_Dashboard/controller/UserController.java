package com.omkar.Finance_Dashboard.controller;

import com.omkar.Finance_Dashboard.model.RoleName;
import com.omkar.Finance_Dashboard.model.User;
import com.omkar.Finance_Dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable UUID id, @RequestParam RoleName newRole) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        return ResponseEntity.ok(userRepository.save(user));
    }
}
