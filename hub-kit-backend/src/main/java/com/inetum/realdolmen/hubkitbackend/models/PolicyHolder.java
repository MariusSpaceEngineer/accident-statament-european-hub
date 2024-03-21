package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "policy_holders")
public class PolicyHolder extends User {
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    @OneToMany
    private List<InsuranceCertificate> insuranceCertificates;
}
