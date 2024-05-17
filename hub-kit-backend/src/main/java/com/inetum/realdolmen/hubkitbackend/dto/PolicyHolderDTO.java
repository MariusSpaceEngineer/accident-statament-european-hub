package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyHolderDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private List<InsuranceCertificateDTO> insuranceCertificates;
}
