package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import lombok.*;

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
    private String policyNumber;
    private String greenCardNumber;
    private LocalDate availabilityDate;
    private LocalDate expirationDate;
    @ManyToOne
    @JoinColumn(name = "insurance_agency_id")
    private InsuranceAgency insuranceAgency;
    @ManyToOne
    @JoinColumn(name = "insurance_company_id")
    private InsuranceCompany insuranceCompany;
}

