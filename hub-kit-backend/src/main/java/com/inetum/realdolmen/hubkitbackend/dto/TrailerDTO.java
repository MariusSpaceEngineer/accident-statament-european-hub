package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrailerDTO {
    private Integer id;
    private String licensePlate;
    private String countryOfRegistration;
}
