package com.inetum.realdolmen.hubkitbackend.controllers;


import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.services.PolicyHolderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final PolicyHolderService service;



    @GetMapping("/profile")
    public ResponseEntity<PolicyHolderDTO> getPolicyHolderProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            Optional<PolicyHolderDTO> userProfile = service.fetchPolicyHolderProfile(token);

            if (userProfile.isPresent()) {
                return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)).body(userProfile.get());
            } else {
                return ResponseEntity.notFound().build(); // User not found
            }
        } else {
            return ResponseEntity.badRequest().build(); // Invalid token
        }
    }

    @PutMapping("profile")
    public ResponseEntity<PolicyHolderDTO> updatePolicyHolderProfile(HttpServletRequest request, @RequestBody PolicyHolderDTO policyHolderDTO){
        String token = extractToken(request);

        if (token != null) {
            Optional<PolicyHolderDTO> userProfile = service.updatePolicyHolderPersonalInformation(token, policyHolderDTO);

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


