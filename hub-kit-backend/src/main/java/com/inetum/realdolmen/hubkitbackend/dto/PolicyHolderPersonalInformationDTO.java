package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
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
    @Email
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
