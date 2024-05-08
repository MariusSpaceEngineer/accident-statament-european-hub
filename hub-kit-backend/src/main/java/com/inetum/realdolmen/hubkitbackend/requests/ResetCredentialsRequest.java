package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.Getter;

@Getter
public class ResetCredentialsRequest {
    private String email;
    private String newPassword;
    private String securityCode;
}
