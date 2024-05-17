package com.inetum.realdolmen.hubkitbackend.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetCredentialsRequest {
    @NotEmpty
    @NotNull
    @Email
    private String email;
    private String newPassword;
    private String securityCode;
}
