package com.inetum.realdolmen.hubkitbackend.controllers;

import com.inetum.realdolmen.hubkitbackend.services.AuthenticationService;
import com.inetum.realdolmen.hubkitbackend.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PolicyHolderRegisterRequest request) {
        try {
            var jwtToken = service.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder().token(jwtToken).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ErrorResponse.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            var jwtToken = service.login(request);
            return ResponseEntity.ok(SuccessResponse.builder().token(jwtToken).build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder().errorMessage(e.getMessage()).build());
        }

    }
}
