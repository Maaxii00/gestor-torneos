package com.maxi.gestortorneos.controller;

import com.maxi.gestortorneos.dto.LoginRequest;
import com.maxi.gestortorneos.dto.LoginResponse;
import com.maxi.gestortorneos.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest peticion) {
        return authService.login(peticion.email(), peticion.password());
    }
}