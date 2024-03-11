package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MotorDTO {
    private Integer id;
    private String brand;
    //TODO: make a enum or a class with values maybe
    private String type;
    private String licensePlate;
    private String countryOfRegistration;
}
