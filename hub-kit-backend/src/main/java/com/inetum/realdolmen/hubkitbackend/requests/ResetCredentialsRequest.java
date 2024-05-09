package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetCredentialsRequest {
    private String email;
    private String newPassword;
    private String securityCode;
}
