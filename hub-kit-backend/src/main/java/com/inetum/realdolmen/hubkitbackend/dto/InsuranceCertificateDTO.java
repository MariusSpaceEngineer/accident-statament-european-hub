package com.inetum.realdolmen.hubkitbackend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inetum.realdolmen.hubkitbackend.utils.VehicleDTODeserializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class InsuranceCertificateDTO {
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
    private InsuranceAgencyDTO insuranceAgency;
    private InsuranceCompanyDTO insuranceCompany;
    @JsonDeserialize(using = VehicleDTODeserializer.class)
    private VehicleDTO vehicle;
}
