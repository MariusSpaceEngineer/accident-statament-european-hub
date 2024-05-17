package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDTO {
    private Integer id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private LocalDate birthday;
    @NotNull
    private String address;
    @NotNull
    private String country;
    private String phoneNumber;
    private String email;
    @NotNull
    private String drivingLicenseNr;
    @NotNull
    private String category;
    @NotNull
    private LocalDate drivingLicenseExpirationDate;
}
