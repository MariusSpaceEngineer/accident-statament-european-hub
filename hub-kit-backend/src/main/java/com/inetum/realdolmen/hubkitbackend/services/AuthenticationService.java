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
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    public String register(PolicyHolderRegisterRequest request) throws Exception {

        if (repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        } else {

            var policyHolder = PolicyHolder.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Roles.POLICY_HOLDER)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .address(request.getAddress())
                    .postalCode(request.getPostalCode())
                    .build();

            repository.save(policyHolder);

            mailService.sendWelcomeMail(policyHolder.getEmail(), policyHolder.getFirstName());

            return jwtService.generateToken(policyHolder);
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

}
