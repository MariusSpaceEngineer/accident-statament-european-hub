package com.inetum.realdolmen.hubkitbackend.dto;

import com.inetum.realdolmen.hubkitbackend.models.Vehicle;
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
    private String policyNumber;
    private String greenCardNumber;
    private LocalDate availabilityDate;
    private LocalDate expirationDate;
    private InsuranceAgencyDTO insuranceAgency;
    private InsuranceCompanyDTO insuranceCompany;
    private Vehicle vehicle;
}
