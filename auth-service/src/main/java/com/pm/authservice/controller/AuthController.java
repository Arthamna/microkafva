package com.pm.authservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.dto.LoginResponseDTO;
import com.pm.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        
        // optional, can accept string or empty values
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO); 
        
        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        
        return ResponseEntity.ok(new LoginResponseDTO(tokenOptional.get()));
    }
    
    @Operation(summary = "Validate token on user login")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {

        // Authorization: Bearer <token> (standard format)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
        }

        // remove "Bearer ", only token
        // if (? true (do smth) : false (do smth))
        return authService.validateToken(authHeader.substring("Bearer ".length()))
        ? ResponseEntity.ok().build() 
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
