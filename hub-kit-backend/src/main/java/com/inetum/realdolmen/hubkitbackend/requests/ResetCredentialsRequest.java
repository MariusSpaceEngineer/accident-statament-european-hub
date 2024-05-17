package com.inetum.realdolmen.hubkitbackend.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
public class ResetCredentialsRequest {
    @NotEmpty
    @NotNull
    private String email;
    private String newPassword;
    private String securityCode;
}
