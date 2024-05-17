package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyHolderDTO {
    private Integer id;
    @NonNull
    @NotEmpty
    private String firstName;
    @NonNull
    @NotEmpty
    private String lastName;
    @Email
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
    private List<InsuranceCertificateDTO> insuranceCertificates;
}
