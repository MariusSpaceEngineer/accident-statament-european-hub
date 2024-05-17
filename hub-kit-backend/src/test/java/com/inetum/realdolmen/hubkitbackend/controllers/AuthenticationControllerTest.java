package com.inetum.realdolmen.hubkitbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.realdolmen.hubkitbackend.exceptions.*;
import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;
import com.inetum.realdolmen.hubkitbackend.services.AuthenticationService;
import com.inetum.realdolmen.hubkitbackend.services.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    private PolicyHolderRegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ResetCredentialsRequest resetCredentialsRequest;
    private String token;

    @BeforeEach
    void setUp() {
        registerRequest = PolicyHolderRegisterRequest.builder().email("example@example.com")
                .firstName("John")
                .lastName("Doe")
                .address("Address")
                .phoneNumber("1561891900")
                .postalCode("1899")
                .password("Example123_")
                .build();

        loginRequest = LoginRequest.builder()
                .email("example@example.com")
                .password("Example123_")
                .build();

        resetCredentialsRequest = ResetCredentialsRequest.builder().email("example@example.com")
                .newPassword("Example_168")
                .securityCode("189874")
                .build();

        token = "validToken";

    }

    @Test
    void registerWithValidRequestReturnsCreated() throws Exception {
        // Arrange
        when(authenticationService.register(any(PolicyHolderRegisterRequest.class))).thenReturn(token);

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is(token)));

        verify(authenticationService, times(1)).register(any(PolicyHolderRegisterRequest.class));
    }

    @Test
    public void registerWithExistingUserReturnsConflict() throws Exception {
        // Arrange
        when(authenticationService.register(any(PolicyHolderRegisterRequest.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is("User already exists")));

        verify(authenticationService, times(1)).register(any(PolicyHolderRegisterRequest.class));
    }

    @Test
    public void registerWithInvalidRequestReturnsBadRequest() throws Exception {
        // Arrange
        when(authenticationService.register(any(PolicyHolderRegisterRequest.class))).thenThrow(new IllegalArgumentException("Invalid request"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("Invalid request")));

        verify(authenticationService, times(1)).register(any(PolicyHolderRegisterRequest.class));
    }

    @Test
    public void registerWithServiceErrorReturnsInternalServerError() throws Exception {
        // Arrange
        when(authenticationService.register(any(PolicyHolderRegisterRequest.class))).thenThrow(new Exception("Internal server error"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage", is("Internal server error")));

        verify(authenticationService, times(1)).register(any(PolicyHolderRegisterRequest.class));
    }

    @Test
    public void loginWithValidRequestReturnsOk() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenReturn(token);

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(token)));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void loginWithInvalidCredentialsReturnsUnauthorized() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage", is("Invalid credentials")));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void loginWithDisabledUserReturnsUnauthorized() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new UserDisabledException("User is disabled"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage", is("User is disabled")));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void loginWithLockedUserReturnsUnauthorized() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new UserLockedException("User account is locked"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage", is("User account is locked")));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void loginWithAuthenticationFailedReturnsUnauthorized() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new AuthenticationFailedException("Authentication failed"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage", is("Authentication failed")));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void loginWithServiceErrorReturnsInternalServerError() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new Exception("Internal server error"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage", is("Internal server error")));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    public void resetPasswordWithValidRequestReturnsOk() throws Exception {
        // Arrange

        String expectedMessage = "Password reset email sent successfully";
        when(authenticationService.resetPassword(anyString())).thenReturn(expectedMessage);

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCredentialsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage", is(expectedMessage)));

        verify(authenticationService, times(1)).resetPassword(anyString());
    }

    @Test
    public void resetPasswordWithNonExistingUserReturnsNotFound() throws Exception {
        // Arrange
        when(authenticationService.resetPassword(anyString())).thenThrow(new UserNotFoundException("User not found"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCredentialsRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.successMessage", is("User not found")));

        verify(authenticationService, times(1)).resetPassword(anyString());
    }

    @Test
    public void resetPasswordWithServiceErrorReturnsInternalServerError() throws Exception {
        // Arrange
        when(authenticationService.resetPassword(anyString())).thenThrow(new Exception("Internal server error"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCredentialsRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage", is("Internal server error")));

        verify(authenticationService, times(1)).resetPassword(anyString());
    }

    @Test
    public void updatePasswordWithValidRequestReturnsOk() throws Exception {
        // Arrange
        String expectedMessage = "Password reset successful";
        when(authenticationService.updatePassword(any(ResetCredentialsRequest.class))).thenReturn(expectedMessage);

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCredentialsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage", is(expectedMessage)));

        verify(authenticationService, times(1)).updatePassword(any(ResetCredentialsRequest.class));
    }

    @Test
    public void updatePasswordWithServiceErrorReturnsBadRequest() throws Exception {
        // Arrange
        when(authenticationService.updatePassword(any(ResetCredentialsRequest.class))).thenThrow(new Exception("Bad request"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/auth/reset/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCredentialsRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("Bad request")));

        verify(authenticationService, times(1)).updatePassword(any(ResetCredentialsRequest.class));
    }
}
