package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AccidentStatementDTO {
    private Integer id;
    @NotNull
    private LocalDateTime date;
    @NotNull
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
    private List<DriverDTO> drivers;
    private WitnessDTO witness;
    @NotNull
    private List<PolicyHolderDTO> policyHolders;
    private List<TrailerDTO> unregisteredTrailers;
    private List<String> vehicleACircumstances;
    private byte[] vehicleAInitialImpactSketch;
    private String vehicleAVisibleDamageDescription;
    private List<AccidentImageDTO> vehicleAAccidentImages;
    private String vehicleARemark;
    @NotNull
    private byte[] vehicleASignature;
    private List<String> vehicleBCircumstances;
    private byte[] vehicleBInitialImpactSketch;
    private String vehicleBVisibleDamageDescription;
    private List<AccidentImageDTO> vehicleBAccidentImages;
    private String vehicleBRemark;
    @NotNull
    private byte[] vehicleBSignature;
}
