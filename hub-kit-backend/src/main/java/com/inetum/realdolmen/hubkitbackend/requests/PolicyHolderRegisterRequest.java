package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.*;

@Getter
@Setter
@Builder
public class PolicyHolderRegisterRequest {
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String phoneNumber;
    @NonNull
    private String address;
    @NonNull
    private String postalCode;
}
