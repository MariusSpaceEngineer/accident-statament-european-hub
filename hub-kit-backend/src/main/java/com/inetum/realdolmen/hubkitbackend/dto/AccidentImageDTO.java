package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentImageDTO {
    private Integer id;
    private byte[] data;
}
