package com.inetum.realdolmen.hubkitbackend.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {
    @NonNull
    @NotEmpty
    @Email
    String email;
    @NonNull
    @NotEmpty
    String password;

}
