package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "insurance_certificates")
public class InsuranceCertificate {
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    @NotEmpty
    private String policyNumber;
    @NotNull
    @NotEmpty
    private String greenCardNumber;
    @NotNull
    private LocalDate availabilityDate;
    @NotNull
    private LocalDate expirationDate;
    @NotNull
    private Boolean materialDamageCovered;
    @ManyToOne
    @JoinColumn(name = "insurance_agency_id")
    private InsuranceAgency insuranceAgency;
    @ManyToOne
    @JoinColumn(name = "insurance_company_id")
    private InsuranceCompany insuranceCompany;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}

