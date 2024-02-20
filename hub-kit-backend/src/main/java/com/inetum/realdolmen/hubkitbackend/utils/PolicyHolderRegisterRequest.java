package com.inetum.realdolmen.hubkitbackend.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyHolderRegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
}
