package com.inetum.realdolmen.hubkitbackend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inetum.realdolmen.hubkitbackend.utils.VehicleDTODeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonDeserialize(using = VehicleDTODeserializer.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class VehicleDTO {
    private Integer id;
    private String licensePlate;
    private String countryOfRegistration;
}
