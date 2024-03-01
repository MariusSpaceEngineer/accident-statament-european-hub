package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PolicyHolderPersonalInformationDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String postalCode;
    private String phoneNumber;
}
