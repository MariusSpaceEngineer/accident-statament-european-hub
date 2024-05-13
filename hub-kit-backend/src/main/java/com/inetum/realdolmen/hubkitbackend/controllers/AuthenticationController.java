package com.inetum.realdolmen.hubkitbackend.controllers;

import com.inetum.realdolmen.hubkitbackend.exceptions.AuthenticationFailedException;
import com.inetum.realdolmen.hubkitbackend.exceptions.InvalidCredentialsException;
import com.inetum.realdolmen.hubkitbackend.exceptions.UserDisabledException;
import com.inetum.realdolmen.hubkitbackend.exceptions.UserLockedException;
import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;
import com.inetum.realdolmen.hubkitbackend.responses.AuthenticationResponse;
import com.inetum.realdolmen.hubkitbackend.responses.Response;
import com.inetum.realdolmen.hubkitbackend.services.AuthenticationService;
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
            return ResponseEntity.status(HttpStatus.CREATED).body(AuthenticationResponse.builder().token(jwtToken).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthenticationResponse.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            var jwtToken = service.login(request);
            return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).build());
        } catch (InvalidCredentialsException | UserDisabledException | UserLockedException |
                 AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder().errorMessage(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthenticationResponse.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetCredentialsRequest request) {
        try {
            service.resetPassword(request.getEmail());
            return ResponseEntity.ok(Response.builder().successMessage("Password reset email sent successfully").build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Response.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/reset/password")
    public ResponseEntity<?> updatePassword(@RequestBody ResetCredentialsRequest request) {
        try {
            service.updatePassword(request);
            return ResponseEntity.ok(Response.builder().successMessage("Password reset successful").build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Response.builder().errorMessage(e.getMessage()).build());
        }
    }

}
