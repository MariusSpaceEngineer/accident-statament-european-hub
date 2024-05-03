package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime date;
    private String location;
    private Boolean injured;
    private Boolean damageToOtherCars;
    private Boolean damageToObjects;
    private Integer numberOfCircumstances;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] sketchOfAccident;
    @ManyToMany
    private List<Driver> drivers;
    @ManyToOne
    private Witness witness;
    @ManyToMany
    private List<PolicyHolder> policyHolders;
    @ManyToMany
    private List<Trailer> unregisteredTrailers;
    @ElementCollection
    private List<String> vehicleACircumstances;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleAInitialImpactSketch;
    private String vehicleAVisibleDamageDescription;
    @OneToMany
    private List<AccidentImage> vehicleAAccidentImages;
    private String vehicleARemark;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleASignature;
    @ElementCollection
    private List<String> vehicleBCircumstances;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleBInitialImpactSketch;
    private String vehicleBVisibleDamageDescription;
    @OneToMany
    private List<AccidentImage> vehicleBAccidentImages;
    private String vehicleBRemark;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleBSignature;
}
