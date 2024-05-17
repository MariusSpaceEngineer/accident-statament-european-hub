package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDTO {
    private Integer id;
    @NonNull
    @NotEmpty
    private String firstName;
    @NonNull
    @NotEmpty
    private String lastName;
    @NonNull
    private LocalDate birthday;
    @NonNull
    @NotEmpty
    private String address;
    @NonNull
    @NotEmpty
    private String country;
    @NonNull
    @NotEmpty
    private String phoneNumber;
    @Email
    @NonNull
    @NotEmpty
    private String email;
    @NonNull
    @NotEmpty
    private String drivingLicenseNr;
    @NonNull
    @NotEmpty
    private String category;
    @NonNull
    private LocalDate drivingLicenseExpirationDate;
}
