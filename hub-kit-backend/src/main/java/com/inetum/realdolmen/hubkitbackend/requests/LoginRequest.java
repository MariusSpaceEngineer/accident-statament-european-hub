package com.inetum.realdolmen.hubkitbackend.requests;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {
    @NonNull
    String email;
    @NonNull
    String password;

}
