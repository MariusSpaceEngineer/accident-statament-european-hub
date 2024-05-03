package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TrailerDTO extends VehicleDTO {
    private Boolean hasRegistration;
    private String ofVehicle;
}
