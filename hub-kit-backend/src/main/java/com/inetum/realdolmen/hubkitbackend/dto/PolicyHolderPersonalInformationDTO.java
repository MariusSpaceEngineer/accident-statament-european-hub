package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PolicyHolderPersonalInformationDTO {
    private Integer id;
    @NonNull
    @NotEmpty
    private String firstName;
    @NonNull
    @NotEmpty
    private String lastName;
    @NonNull
    @NotEmpty
    private String email;
    @NonNull
    @NotEmpty
    private String address;
    @NonNull
    @NotEmpty
    private String postalCode;
    @NonNull
    @NotEmpty
    private String phoneNumber;
}
