package com.inetum.realdolmen.hubkitbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceCompanyDTO {
    private Integer id;
    @NonNull
    @NotEmpty
    private String name;
}
