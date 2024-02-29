package com.inetum.realdolmen.hubkitbackend.controllers;


import com.inetum.realdolmen.hubkitbackend.dto.UserProfileDTO;
import com.inetum.realdolmen.hubkitbackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;



    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            Optional<UserProfileDTO> userProfile = service.fetchUserProfile(token);

            if (userProfile.isPresent()) {
                return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)).body(userProfile.get());
            } else {
                return ResponseEntity.notFound().build(); // User not found
            }
        } else {
            return ResponseEntity.badRequest().build(); // Invalid token
        }
    }



    private String extractToken(HttpServletRequest request) {
        // Extract the token from the authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return token;
        } else {
            return null;
        }
    }

}


