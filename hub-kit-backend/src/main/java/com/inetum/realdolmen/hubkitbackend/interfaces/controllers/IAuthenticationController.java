package com.inetum.realdolmen.hubkitbackend.interfaces.controllers;

import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;
import com.inetum.realdolmen.hubkitbackend.responses.AuthenticationResponse;
import com.inetum.realdolmen.hubkitbackend.responses.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthenticationController {
    ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody PolicyHolderRegisterRequest request);

    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request);

    ResponseEntity<Response> resetPassword(@RequestBody @Valid ResetCredentialsRequest request);

    ResponseEntity<Response> updatePassword(@RequestBody @Valid ResetCredentialsRequest request);
}
