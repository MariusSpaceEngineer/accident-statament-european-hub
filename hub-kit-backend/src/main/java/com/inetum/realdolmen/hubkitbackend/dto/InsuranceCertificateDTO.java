package com.inetum.realdolmen.hubkitbackend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
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
    private Boolean materialDamageCovered;
    private InsuranceAgencyDTO insuranceAgency;
    private InsuranceCompanyDTO insuranceCompany;
    @JsonDeserialize(using = VehicleDTODeserializer.class)
    private VehicleDTO vehicle;
}
