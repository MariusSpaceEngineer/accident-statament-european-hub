package com.inetum.realdolmen.hubkitbackend.interfaces.controllers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.dto.LocationCoordinates;
import com.inetum.realdolmen.hubkitbackend.responses.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAccidentStatementController {
    ResponseEntity<Response> createStatement(@RequestBody @Valid AccidentStatementDTO accidentStatementDTO);
    ResponseEntity<Response> getAccidentLocation(@RequestBody @Valid LocationCoordinates locationCoordinates);
}
