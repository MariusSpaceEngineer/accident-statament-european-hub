package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentStatementDTO {
    private Integer id;
    @NotNull
    private LocalDateTime date;
    @NotNull
    @NotEmpty
    private String location;
    @NotNull
    private Boolean injured;
    @NotNull
    private Boolean damageToOtherCars;
    @NotNull
    private Boolean damageToObjects;
    @NotNull
    private Integer numberOfCircumstances;
    private byte[] sketchOfAccident;
    @NotNull
    @NotEmpty
    private List<DriverDTO> drivers;
    private WitnessDTO witness;
    @NotNull
    @NotEmpty
    private List<PolicyHolderDTO> policyHolders;
    private List<TrailerDTO> unregisteredTrailers;
    private List<String> vehicleACircumstances;
    private byte[] vehicleAInitialImpactSketch;
    @Size(max = 250)
    private String vehicleAVisibleDamageDescription;
    private List<AccidentImageDTO> vehicleAAccidentImages;
    private String vehicleARemark;
    @NotNull
    @NotEmpty
    private byte[] vehicleASignature;
    private List<String> vehicleBCircumstances;
    private byte[] vehicleBInitialImpactSketch;
    @Size(max = 250)
    private String vehicleBVisibleDamageDescription;
    private List<AccidentImageDTO> vehicleBAccidentImages;
    private String vehicleBRemark;
    @NotNull
    @NotEmpty
    private byte[] vehicleBSignature;
}
