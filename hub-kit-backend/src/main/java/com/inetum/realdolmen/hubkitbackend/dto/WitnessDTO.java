package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WitnessDTO {
    private Integer id;
    @NonNull
    @NotEmpty
    private String name;
    @NonNull
    @NotEmpty
    private String address;
    @NonNull
    @NotEmpty
    private String phoneNumber;
}
