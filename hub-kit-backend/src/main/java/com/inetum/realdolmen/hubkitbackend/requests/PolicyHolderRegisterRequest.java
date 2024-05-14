package com.inetum.realdolmen.hubkitbackend.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
public class PolicyHolderRegisterRequest {
    @NonNull
    @NotEmpty
    @Email
    private String email;
    @NonNull
    @NotEmpty
    private String password;
    @NonNull
    @NotEmpty
    private String firstName;
    @NonNull
    @NotEmpty
    private String lastName;
    @NonNull
    @NotEmpty
    private String phoneNumber;
    @NonNull
    @NotEmpty
    private String address;
    @NonNull
    @NotEmpty
    private String postalCode;
}
