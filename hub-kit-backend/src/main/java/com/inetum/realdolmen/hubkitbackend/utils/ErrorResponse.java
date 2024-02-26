package com.inetum.realdolmen.hubkitbackend.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private String errorMessage;

}
