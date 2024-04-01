package com.inetum.realdolmen.hubkitbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocationCoordinates {
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;
}
