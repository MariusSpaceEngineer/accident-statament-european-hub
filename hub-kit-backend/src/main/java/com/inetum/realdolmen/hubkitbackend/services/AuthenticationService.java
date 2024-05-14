package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.utils.Roles;
import com.inetum.realdolmen.hubkitbackend.exceptions.*;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    public String register(PolicyHolderRegisterRequest request) throws Exception {
        try{
            if (repository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("An user already exists with the given email, please try another one.");
            } else {
                if (validatePassword(request.getPassword()) != null){
                    throw new IllegalArgumentException("Password does not meet the required criteria.");
                }
                var policyHolder = PolicyHolder.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Roles.POLICY_HOLDER)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .phoneNumber(request.getPhoneNumber())
                        .address(request.getAddress())
                        .postalCode(request.getPostalCode())
                        .build();

                repository.save(policyHolder);

                mailService.sendWelcomeMail(policyHolder.getEmail(), policyHolder.getFirstName());

                return jwtService.generateToken(policyHolder);
            }
        }
        catch (UserAlreadyExistsException e) {
            log.error("User already exists:", e);
            throw e;
        }
        catch (IllegalArgumentException e) {
            log.error("Unexpected error during validation:", e);
            throw e;
        }
        catch (Exception e){
            log.error("Unexpected error during registration", e);
            throw new Exception("Internal server error");
        }
    }

    public String login(LoginRequest request) throws Exception {
        try {
            var user = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword())
            );
            return jwtService.generateToken((UserDetails) user.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            throw new UserDisabledException("User is disabled");
        } catch (LockedException e) {
            throw new UserLockedException("User account is locked");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Authentication failed");
        } catch (Exception e){
            log.error("Unexpected error during authentication", e);
            throw new Exception("Internal server error");
        }
    }

    public void resetPassword(String email) throws Exception {
        var user = repository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        else {
            // Generate a random 6-digit code
            String code = String.format("%06d", new Random().nextInt(999999));

            // Save the code to the user's record
            User userEntity = user.get();
            userEntity.setResetCode(code);
            repository.save(userEntity);

            try {
                mailService.sendResetCodeEmail(userEntity.getEmail(), code);
            } catch (Exception e) {
                throw new Exception("Error sending reset code email", e);
            }
        }
    }

    public void updatePassword(ResetCredentialsRequest resetCredentialsRequest) throws Exception {
        var user = repository.findByEmail(resetCredentialsRequest.getEmail());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        else {
            User userEntity = user.get();
            if (Objects.equals(resetCredentialsRequest.getSecurityCode(), userEntity.getResetCode())) {
                userEntity.setPassword(passwordEncoder.encode(resetCredentialsRequest.getNewPassword()));
                repository.save(userEntity);
            } else {
                throw new Exception("Security code does not match reset code");
            }
        }
    }

    private String validatePassword(String password) {
        List<String> passwordErrors = new ArrayList<>();
        if (password.length() < 6) {
            passwordErrors.add("Password is too short");
        }
        if (!password.matches(".*[A-Z].*")) {
            passwordErrors.add("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            passwordErrors.add("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            passwordErrors.add("Password must contain at least one digit");
        }
        if (!password.matches(".*[@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            passwordErrors.add("Password must contain at least one special character");
        }
        if (password.contains(" ")) {
            passwordErrors.add("Password must not contain whitespace");
        }
        // If no errors, return null
        return passwordErrors.isEmpty() ? null : String.join("\n", passwordErrors);
    }



}
