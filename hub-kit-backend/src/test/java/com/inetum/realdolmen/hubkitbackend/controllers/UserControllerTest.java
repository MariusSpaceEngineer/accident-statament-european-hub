package com.inetum.realdolmen.hubkitbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.services.JwtService;
import com.inetum.realdolmen.hubkitbackend.services.PolicyHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PolicyHolderService policyHolderService;

    @MockBean
    private JwtService jwtService;

    private PolicyHolderDTO policyHolder;
    private PolicyHolderPersonalInformationDTO policyHolderPersonalInformation;

    private  InsuranceAgencyDTO insuranceAgency;
    private InsuranceCompanyDTO insuranceCompany;
    private MotorDTO motorDTO;
    private InsuranceCertificateDTO insuranceCertificate;

    private String token;

    @BeforeEach
    void setUp() {

        policyHolderPersonalInformation = PolicyHolderPersonalInformationDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address("123 Main St")
                .postalCode("12345")
                .phoneNumber("123-456-7890")
                .build();

        insuranceAgency = InsuranceAgencyDTO.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        insuranceCompany = InsuranceCompanyDTO.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        motorDTO = MotorDTO.builder()
                .id(1)
                .licensePlate("K4489ML")
                .countryOfRegistration("Belgium")
                .markType("Volkswagen")
                .build();

        insuranceCertificate = InsuranceCertificateDTO.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.of(2024, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgency)
                .insuranceCompany(insuranceCompany)
                .vehicle(motorDTO)
                .build();

        policyHolder = PolicyHolderDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificate)))
                .build();

        token = "validToken";  // Set the token field, not a local variable
    }

    @Test
    void getPolicyHolderProfileWithValidTokenReturnsOk() throws Exception {
        // Arrange
        when(policyHolderService.fetchPolicyHolderProfile(token)).thenReturn(Optional.of(policyHolder));

        // Act and Assert
        mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(policyHolder)));

        verify(policyHolderService, times(1)).fetchPolicyHolderProfile(token);
    }

    @Test
    void getPolicyHolderProfileWithInvalidTokenReturnsBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isBadRequest());

        verify(policyHolderService, times(0)).fetchPolicyHolderProfile(any());
    }

    @Test
    void getPolicyHolderProfileWithMissingTokenReturnsBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isBadRequest());

        verify(policyHolderService, times(0)).fetchPolicyHolderProfile(any());
    }

    @Test
    void getPolicyHolderProfileWithValidTokenReturnsNotFound() throws Exception {
        // Arrange
        when(policyHolderService.fetchPolicyHolderProfile(token)).thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        verify(policyHolderService, times(1)).fetchPolicyHolderProfile(token);
    }

    @Test
    void getPolicyHolderProfileWithValidTokenThrowsExceptionReturnsInternalServerError() throws Exception {
        // Arrange
        when(policyHolderService.fetchPolicyHolderProfile(token)).thenThrow(new RuntimeException());

        // Act and Assert
        mockMvc.perform(get("/api/v1/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError());

        verify(policyHolderService, times(1)).fetchPolicyHolderProfile(token);
    }

    @Test
    void updatePolicyHolderPersonalInformationWithValidTokenReturnsOk() throws Exception {
        // Arrange
        var updatedPolicyHolderPersonalInformation = PolicyHolderPersonalInformationDTO.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address("123 Main St")
                .postalCode("12345")
                .phoneNumber("123-456-7890")
                .build();
        when(policyHolderService.updatePolicyHolderPersonalInformation(any(String.class), any(PolicyHolderPersonalInformationDTO.class))).thenReturn(Optional.of(updatedPolicyHolderPersonalInformation));

        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/personal")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyHolderPersonalInformation)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(updatedPolicyHolderPersonalInformation)));

    }

    @Test
    void updatePolicyHolderPersonalInformationWithInvalidTokenReturnsBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/personal")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyHolderPersonalInformation)))
                .andExpect(status().isBadRequest());

        verify(policyHolderService, times(0)).updatePolicyHolderPersonalInformation(any(), any());
    }

    @Test
    void updatePolicyHolderPersonalInformationWithValidTokenReturnsNotFound() throws Exception {
        // Arrange
        when(policyHolderService.updatePolicyHolderPersonalInformation(any(String.class), any(PolicyHolderPersonalInformationDTO.class))).thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/personal")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyHolderPersonalInformation)))
                .andExpect(status().isNotFound());

        verify(policyHolderService, times(1)).updatePolicyHolderPersonalInformation(token, policyHolderPersonalInformation);
    }

    @Test
    void updatePolicyHolderInsuranceInformationWithValidTokenReturnsOk() throws Exception {
        // Arrange
        var updateInsuranceCertificate = InsuranceCertificateDTO.builder()
                .id(2)
                .policyNumber("Policy12344")
                .greenCardNumber("Green12673")
                .availabilityDate(LocalDate.of(2025, 1, 1))
                .expirationDate(LocalDate.of(2025, 1, 1))
                .materialDamageCovered(true)
                .insuranceAgency(insuranceAgency)
                .insuranceCompany(insuranceCompany)
                .vehicle(motorDTO)
                .build();
        when(policyHolderService.updateInsuranceCertificateInformation(any(String.class), any(InsuranceCertificateDTO.class)))
                .thenReturn(Optional.of(Collections.singletonList(updateInsuranceCertificate)));

        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/insurance")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insuranceCertificate)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(updateInsuranceCertificate))));

    }

    @Test
    void updatePolicyHolderInsuranceInformationWithInvalidTokenReturnsBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/insurance")
                        .header("Authorization", "Bearer ")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insuranceCertificate)))
                .andExpect(status().isBadRequest());

        verify(policyHolderService, times(0)).updateInsuranceCertificateInformation(any(), any());
    }

    @Test
    void updatePolicyHolderInsuranceInformationWithValidTokenReturnsNotFound() throws Exception {
        // Arrange
        when(policyHolderService.updateInsuranceCertificateInformation(any(String.class), any(InsuranceCertificateDTO.class))).thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(put("/api/v1/user/profile/insurance")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(insuranceCertificate)))
                .andExpect(status().isNotFound());

    }
}

