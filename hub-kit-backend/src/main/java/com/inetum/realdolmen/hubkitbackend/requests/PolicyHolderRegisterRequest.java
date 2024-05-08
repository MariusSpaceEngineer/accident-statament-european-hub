package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.*;

@Getter
@Setter
@Builder
public class PolicyHolderRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
}
