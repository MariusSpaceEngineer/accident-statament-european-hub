package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceAgencyDTO {
    private Integer id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String country;
    @NotNull
    @NotEmpty
    private String phoneNumber;
    @Email
    @NotNull
    @NotEmpty
    private String email;
}
