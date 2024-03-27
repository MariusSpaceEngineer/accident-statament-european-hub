package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class InsuranceCertificateDTO {
    private Integer id;
    @NonNull
    private String policyNumber;
    @NonNull
    private String greenCardNumber;
    @NonNull
    private LocalDate availabilityDate;
    @NonNull
    private LocalDate expirationDate;
    @NonNull
    private InsuranceAgencyDTO insuranceAgency;
    @NonNull
    private InsuranceCompanyDTO insuranceCompany;
}
