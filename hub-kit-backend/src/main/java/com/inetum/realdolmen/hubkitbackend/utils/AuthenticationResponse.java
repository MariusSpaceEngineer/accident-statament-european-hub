package com.inetum.realdolmen.hubkitbackend.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {

    private String token;
    private String errorMessage;
}
