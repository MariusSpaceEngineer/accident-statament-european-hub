package com.inetum.realdolmen.hubkitbackend.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class Response {
    @JsonInclude(Include.NON_NULL)
    private String successMessage;
    @JsonInclude(Include.NON_NULL)
    private String errorMessage;
}
