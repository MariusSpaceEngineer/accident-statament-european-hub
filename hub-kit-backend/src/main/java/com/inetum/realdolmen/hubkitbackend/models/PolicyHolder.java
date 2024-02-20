package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "policy_holders")
public class PolicyHolder extends User {
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;

}
