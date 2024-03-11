package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class DriverDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String address;
    private String country;
    private String phoneNumber;
    private String email;
    private String drivingLicenseNr;
    private String category;
    private LocalDate drivingLicenseExpirationDate;
}
