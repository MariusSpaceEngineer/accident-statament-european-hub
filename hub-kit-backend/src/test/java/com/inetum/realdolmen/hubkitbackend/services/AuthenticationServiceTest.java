package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.exceptions.*;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;
import com.inetum.realdolmen.hubkitbackend.utils.Roles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthenticationService service;

    @Test
    public void testRegister() throws Exception {
        // Arrange
        PolicyHolderRegisterRequest request = PolicyHolderRegisterRequest.builder()
                .email("test@example.com")
                .password("Example_123")
                .firstName("Test")
                .lastName("Name")
                .phoneNumber("0161891919")
                .address("Address")
                .postalCode("18919")
                .build();

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(any())).thenReturn("token");

        // Act
        String result = service.register(request);

        // Assert
        assertEquals("token", result);
        verify(mailService, times(1)).sendWelcomeMail(request.getEmail(), request.getFirstName());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testRegisterUserAlreadyExists() throws Exception {
        // Arrange
        PolicyHolderRegisterRequest request = PolicyHolderRegisterRequest.builder()
                .email("test@example.com")
                .password("Example_123")
                .firstName("Test")
                .lastName("Name")
                .phoneNumber("0161891919")
                .address("Address")
                .postalCode("18919")
                .build();

        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act
        service.register(request);
    }

    @Test
    public void testLogin() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Example_123").build();

        UserDetails userDetails = User.builder()
                .email("test@example.com")
                .password(request.getPassword())
                .role(Roles.POLICY_HOLDER)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken(userDetails, null));
        when(jwtService.generateToken(any())).thenReturn("token");

        // Act
        String result = service.login(request);

        // Assert
        assertEquals("token", result);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testLoginInvalidCredentials() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrong_password").build();

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        service.login(request);
    }

    @Test(expected = UserDisabledException.class)
    public void testLoginUserDisabled() throws Exception {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Example_123").build();

        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("User is disabled"));

        // Act
        service.login(request);
    }

    @Test
    public void testResetPassword() throws Exception {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        String result = service.resetPassword(email);

        // Assert
        assertEquals("Password reset email sent successfully", result);
    }

    @Test
    public void testUpdatePassword() throws Exception {
        // Arrange
        ResetCredentialsRequest request = ResetCredentialsRequest.builder()
                .email("test@example.com")
                .newPassword("New_Password_99")
                .securityCode("189198")
                .build();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setResetCode(request.getSecurityCode());

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedPassword");

        // Act
        String result = service.updatePassword(request);

        // Assert
        assertEquals("Password reset successful", result);
    }

    @Test(expected = UserNotFoundException.class)
    public void testResetPasswordUserNotFound() throws Exception {
        // Arrange
        String email = "nonexistent@example.com";

        when(repository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        service.resetPassword(email);
    }

    // Additional test for updatePassword method when security code does not match
    @Test(expected = Exception.class)
    public void testUpdatePasswordSecurityCodeMismatch() throws Exception {
        // Arrange
        ResetCredentialsRequest request = ResetCredentialsRequest.builder()
                .email("test@example.com")
                .newPassword("New_Password_99")
                .securityCode("wrong_code")
                .build();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setResetCode("correct_code");

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // Act
        service.updatePassword(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterPasswordTooShort() throws Exception {
        // Arrange
        PolicyHolderRegisterRequest request = PolicyHolderRegisterRequest.builder()
                .email("test@example.com")
                .password("Ex_1") // Password is too short
                .firstName("Test")
                .lastName("Name")
                .phoneNumber("0161891919")
                .address("Address")
                .postalCode("18919")
                .build();

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);

        // Act
        service.register(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterPasswordNoUppercase() throws Exception {
        // Arrange
        PolicyHolderRegisterRequest request = PolicyHolderRegisterRequest.builder()
                .email("test@example.com")
                .password("example_123") // Password does not contain an uppercase letter
                .firstName("Test")
                .lastName("Name")
                .phoneNumber("0161891919")
                .address("Address")
                .postalCode("18919")
                .build();

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);

        // Act
        service.register(request);
    }
}
