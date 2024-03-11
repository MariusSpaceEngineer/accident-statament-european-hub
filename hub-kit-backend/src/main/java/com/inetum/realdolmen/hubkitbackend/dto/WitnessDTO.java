package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WitnessDTO {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
}
