package com.inetum.realdolmen.hubkitbackend.services;

import com.inetum.realdolmen.hubkitbackend.Roles;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.inetum.realdolmen.hubkitbackend.utils.AuthenticationRequest;
import com.inetum.realdolmen.hubkitbackend.utils.AuthenticationResponse;
import com.inetum.realdolmen.hubkitbackend.utils.PolicyHolderRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(PolicyHolderRegisterRequest request) {

        if (repository.existsByEmail(request.getEmail())){
            return AuthenticationResponse.builder()
                    .errorMessage("User already exists")
                    .build();
        }
        else {

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

            var jwtToken = jwtService.generateToken(policyHolder);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        //Throws an error if the user is not found
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
