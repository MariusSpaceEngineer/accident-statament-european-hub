package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "accident_statements")
public class AccidentStatement {
    @Id
    @GeneratedValue
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
    private String remarkVehicleA;
    private String remarkVehicleB;
    private String visibleDamageVehicleA;
    private String visibleDamageVehicleB;
    private Byte signatureVehicleA;
    private Byte signatureVehicleB;
    @ManyToMany
    private List<Driver> drivers;
    @ManyToMany
    private List<Witness> witnesses;
    @ManyToMany
    private List<Motor> motors;
    @ManyToMany
    private List<PolicyHolder> policyHolders;
    @ManyToMany
    private List<Trailer> trailers;
}
