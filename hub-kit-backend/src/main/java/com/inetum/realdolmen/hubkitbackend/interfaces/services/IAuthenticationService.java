package com.inetum.realdolmen.hubkitbackend.interfaces.services;

import com.inetum.realdolmen.hubkitbackend.requests.LoginRequest;
import com.inetum.realdolmen.hubkitbackend.requests.PolicyHolderRegisterRequest;
import com.inetum.realdolmen.hubkitbackend.requests.ResetCredentialsRequest;

public interface IAuthenticationService {
    String register(PolicyHolderRegisterRequest request) throws Exception;

    String login(LoginRequest request) throws Exception;

    String resetPassword(String email) throws Exception;

    String updatePassword(ResetCredentialsRequest resetCredentialsRequest) throws Exception;

}
