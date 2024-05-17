package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceAgencyDTO {
    private Integer id;
    private String name;
    private String address;
    private String country;
    private String phoneNumber;
    private String email;
}
