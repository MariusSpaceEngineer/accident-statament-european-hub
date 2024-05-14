package com.inetum.realdolmen.hubkitbackend.controllers;


import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import com.inetum.realdolmen.hubkitbackend.exceptions.VehicleMismatchException;
import com.inetum.realdolmen.hubkitbackend.services.PolicyHolderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final PolicyHolderService service;

    @GetMapping("/profile")
    public ResponseEntity<PolicyHolderDTO> getPolicyHolderProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            try {
                Optional<PolicyHolderDTO> userProfile = service.fetchPolicyHolderProfile(token);
                return userProfile.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

            } catch (Exception e) {
                log.error("Error while fetching policy holder profile:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Invalid token
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/profile/personal")
    public ResponseEntity<PolicyHolderPersonalInformationDTO> updatePolicyHolderPersonalInformation(HttpServletRequest request, @Valid @RequestBody PolicyHolderPersonalInformationDTO policyHolderDTO) {
        String token = extractToken(request);
        if (token != null) {
            try {
                Optional<PolicyHolderPersonalInformationDTO> policyHolderPersonalInformationDTO = service.updatePolicyHolderPersonalInformation(token, policyHolderDTO);
                return policyHolderPersonalInformationDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
            } catch (Exception e) {
                log.error("Error while updating policy holder personal information:", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Invalid token
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/profile/insurance")
    public ResponseEntity<?> updatePolicyHolderInsuranceInformation(HttpServletRequest request, @RequestBody InsuranceCertificateDTO insuranceCertificateDTO) {
        String token = extractToken(request);

        if (token != null) {
            try {
                Optional<List<InsuranceCertificateDTO>> insuranceCertificate = service.updateInsuranceCertificateInformation(token, insuranceCertificateDTO);
                return insuranceCertificate.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
            } catch (VehicleMismatchException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            // Invalid token
            return ResponseEntity.badRequest().build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        // Extract the token from the authorization header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            return null;
        }
    }
}


