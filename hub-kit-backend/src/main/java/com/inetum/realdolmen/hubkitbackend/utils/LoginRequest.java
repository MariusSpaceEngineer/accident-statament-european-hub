package com.inetum.realdolmen.hubkitbackend.utils;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {
    String email;
    String password;

}
