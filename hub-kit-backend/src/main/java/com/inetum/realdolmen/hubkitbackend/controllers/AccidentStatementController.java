package com.inetum.realdolmen.hubkitbackend.controllers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.dto.LocationCoordinates;
import com.inetum.realdolmen.hubkitbackend.services.AccidentStatementService;
import com.inetum.realdolmen.hubkitbackend.responses.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statement")
@RequiredArgsConstructor
public class AccidentStatementController {
    private final AccidentStatementService service;

    @PostMapping("/create")
    public ResponseEntity<Response> createStatement(@RequestBody @Valid AccidentStatementDTO accidentStatementDTO) {
        try {
            var response = service.createAccidentStatement(accidentStatementDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder().successMessage(response).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Response.builder().errorMessage(e.getMessage()).build());
        }
    }

    @PostMapping("/accident/location")
    public ResponseEntity<Response> getAccidentLocation(@RequestBody @Valid LocationCoordinates locationCoordinates) {
        try {
            var response = service.getLocationAddress(locationCoordinates);
            return ResponseEntity.status(HttpStatus.OK).body(Response.builder().successMessage(response).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder().errorMessage(e.getMessage()).build());
        }
    }
}
