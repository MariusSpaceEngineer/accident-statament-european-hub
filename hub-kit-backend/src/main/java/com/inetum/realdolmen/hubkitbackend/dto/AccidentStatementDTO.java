package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class AccidentStatementDTO {
    private Integer id;
    private LocalDate date;
    private String location;
    private Boolean injured;
    private Boolean damageToOtherCars;
    private Boolean damageToObjects;
    //TODO: create circumstances class
    //private Set<?> circumstances;
    private Integer numberOfCircumstances;
    private Byte sketchOfImage;
    private Byte initialImpactVehicleA;
    private Byte initialImpactVehicleB;
    private byte[] vehicleAAccidentImage;
    private String remarkVehicleA;
    private byte[] vehicleBAccidentImage;
    private String remarkVehicleB;
    private String visibleDamageVehicleA;
    private String visibleDamageVehicleB;
    private Byte signatureVehicleA;
    private Byte signatureVehicleB;
    private List<DriverDTO> drivers;
    private List<WitnessDTO> witnesses;
    private List<PolicyHolderDTO> policyHolders;
    private List<MotorDTO> motors;
    private List<TrailerDTO> trailers;
}
