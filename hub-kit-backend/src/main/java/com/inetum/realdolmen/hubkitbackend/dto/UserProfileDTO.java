package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
//TODO: add all the needed fields
public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String postalCode;
}
