package com.inetum.realdolmen.hubkitbackend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "trailers")
public class Trailer {
    @Id
    @GeneratedValue
    private Integer id;
    private Boolean hasRegistration;
    private String licensePlate;
    private String countryOfRegistration;
}
