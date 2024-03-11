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
@Table(name = "motors")
public class Motor {
    @Id
    @GeneratedValue
    private Integer id;
    private String brand;
    //TODO: make a enum or a class with values maybe
    private String type;
    private String licensePlate;
    private String countryOfRegistration;
}
