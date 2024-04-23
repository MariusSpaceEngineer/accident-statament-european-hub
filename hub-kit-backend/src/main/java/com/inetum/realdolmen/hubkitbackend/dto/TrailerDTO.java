package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@Builder
public class TrailerDTO {
    private Integer id;
    @NotNull
    private Boolean hasRegistration;
    private String licensePlate;
    private String countryOfRegistration;
}
