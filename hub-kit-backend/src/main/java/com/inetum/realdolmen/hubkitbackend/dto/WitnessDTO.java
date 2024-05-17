package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WitnessDTO {
    private Integer id;
    private String name;
    private String address;
    private String phoneNumber;
}
