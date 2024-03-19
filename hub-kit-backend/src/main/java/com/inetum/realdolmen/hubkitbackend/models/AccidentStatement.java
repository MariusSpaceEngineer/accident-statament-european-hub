package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    @Lob
    @Column(name = "vehicle_a_accident_image", columnDefinition="BLOB")
    private byte[] vehicleAAccidentImage;
    private String remarkVehicleA;
    @Lob
    @Column(name = "vehicle_b_accident_image", columnDefinition="BLOB")
    private byte[] vehicleBAccidentImage;
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
