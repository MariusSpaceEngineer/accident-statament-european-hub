package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.Roles;
import com.inetum.realdolmen.hubkitbackend.exceptions.*;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.inetum.realdolmen.hubkitbackend.utils.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.utils.MailService;
import com.inetum.realdolmen.hubkitbackend.utils.PolicyHolderRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
