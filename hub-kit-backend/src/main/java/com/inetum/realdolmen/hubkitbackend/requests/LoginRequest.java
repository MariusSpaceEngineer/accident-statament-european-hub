package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {
    String email;
    String password;

}
