package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue
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
