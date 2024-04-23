package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Builder
public class MotorDTO {
    private Integer id;
    @NotNull
    private String markType;
    @NotNull
    private String licensePlate;
    @NotNull
    private String countryOfRegistration;
}
