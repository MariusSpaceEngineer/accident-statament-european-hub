package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "insurance_agencies")
public class InsuranceAgency {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String address;
    private String country;
    private String phoneNumber;
    private String email;
}
