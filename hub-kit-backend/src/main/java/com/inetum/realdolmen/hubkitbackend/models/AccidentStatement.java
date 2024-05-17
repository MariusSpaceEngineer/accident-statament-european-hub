package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] sketchOfAccident;
    @NotNull
    @NotEmpty
    @ManyToMany
    private List<Driver> drivers;
    @ManyToOne
    private Witness witness;
    @NotNull
    @NotEmpty
    @ManyToMany
    private List<PolicyHolder> policyHolders;
    @ManyToMany
    private List<Trailer> unregisteredTrailers;
    @ElementCollection
    private List<String> vehicleACircumstances;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleAInitialImpactSketch;
    @Size(max = 250)
    private String vehicleAVisibleDamageDescription;
    @OneToMany
    private List<AccidentImage> vehicleAAccidentImages;
    private String vehicleARemark;
    @NotNull
    @NotEmpty
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleASignature;
    @ElementCollection
    private List<String> vehicleBCircumstances;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleBInitialImpactSketch;
    @Size(max = 250)
    private String vehicleBVisibleDamageDescription;
    @OneToMany
    private List<AccidentImage> vehicleBAccidentImages;
    private String vehicleBRemark;
    @NotNull
    @NotEmpty
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] vehicleBSignature;
}
