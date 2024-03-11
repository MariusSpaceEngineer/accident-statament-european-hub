package com.inetum.realdolmen.hubkitbackend.controllers;

import com.inetum.realdolmen.hubkitbackend.dto.AccidentStatementDTO;
import com.inetum.realdolmen.hubkitbackend.services.AccidentStatementService;
import com.inetum.realdolmen.hubkitbackend.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statement")
@RequiredArgsConstructor
public class AccidentStatementController {
    private final AccidentStatementService service;

    @PostMapping("/create")
    public ResponseEntity<?> createStatement (@RequestBody AccidentStatementDTO accidentStatementDTO){
        try{
            var response = service.createAccidentStatement(accidentStatementDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(Response.builder().successMessage(response).build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Response.builder().errorMessage(e.getMessage()).build());
        }
    }
}
