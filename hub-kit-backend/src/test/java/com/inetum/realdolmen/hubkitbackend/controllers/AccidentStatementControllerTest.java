package com.inetum.realdolmen.hubkitbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.realdolmen.hubkitbackend.dto.*;
import com.inetum.realdolmen.hubkitbackend.services.AccidentStatementService;
import com.inetum.realdolmen.hubkitbackend.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccidentStatementController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccidentStatementControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private AccidentStatementService accidentStatementService;

    @MockBean
    private JwtService jwtService;

    private AccidentStatementDTO accidentStatement;
    private LocationCoordinates locationCoordinates;

    @BeforeEach
    void setUp() {
        DriverDTO driverA = DriverDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .birthday(LocalDate.parse("1980-01-01"))
                .address("123 Main St")
                .country("Belgium")
                .phoneNumber("1234567890")
                .email("john.doe@example.com")
                .drivingLicenseNr("123456")
                .category("B")
                .drivingLicenseExpirationDate(LocalDate.parse("2030-01-01"))
                .build();

        DriverDTO driverB = DriverDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .birthday(LocalDate.parse("1990-02-02"))
                .address("456 Secondary St")
                .country("Belgium")
                .phoneNumber("0987654321")
                .email("jane.smith@example.com")
                .drivingLicenseNr("654321")
                .category("B")
                .drivingLicenseExpirationDate(LocalDate.parse("2035-02-02"))
                .build();

        List<AccidentImageDTO> accidentImagesVehicleA = Collections.singletonList(AccidentImageDTO.builder().data(new byte[123]).build());
        List<AccidentImageDTO> accidentImagesVehicleB = Collections.singletonList(AccidentImageDTO.builder().data(new byte[124]).build());

        WitnessDTO witness = WitnessDTO.builder()
                .name("Witness Name")
                .address("456 Witness St")
                .phoneNumber("0987654321")
                .build();

        MotorDTO motorA = MotorDTO.builder()
                .markType("Brand")
                .licensePlate("1LOI958")
                .countryOfRegistration("Belgium")
                .build();

        MotorDTO motorB = MotorDTO.builder()
                .markType("Brand")
                .licensePlate("2LOI958")
                .countryOfRegistration("Belgium")
                .build();

        TrailerDTO registeredTrailerA = TrailerDTO.builder()
                .hasRegistration(true)
                .licensePlate("DEF456")
                .countryOfRegistration("Belgium")
                .build();

        TrailerDTO unregisteredTrailerB = TrailerDTO.builder()
                .hasRegistration(false)
                .ofVehicle("Vehicle B")
                .build();

        InsuranceAgencyDTO insuranceAgencyPolicyHolderA = InsuranceAgencyDTO.builder()
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        InsuranceCompanyDTO insuranceCompanyPolicyHolderA = InsuranceCompanyDTO.builder()
                .name("Insurance Company")
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderAMotor = InsuranceCertificateDTO.builder()
                .policyNumber("PO54894948")
                .greenCardNumber("GC498491989")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .materialDamageCovered(false)
                .insuranceAgency(insuranceAgencyPolicyHolderA)
                .insuranceCompany(insuranceCompanyPolicyHolderA)
                .vehicle(motorA)
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderATrailer = InsuranceCertificateDTO.builder()
                .policyNumber("PO2294988")
                .greenCardNumber("GC594984919")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .insuranceAgency(insuranceAgencyPolicyHolderA)
                .materialDamageCovered(true)
                .insuranceCompany(insuranceCompanyPolicyHolderA)
                .vehicle(registeredTrailerA)
                .build();

        PolicyHolderDTO policyHolderVehicleA = PolicyHolderDTO.builder()
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificatePolicyHolderAMotor, insuranceCertificatePolicyHolderATrailer)))
                .build();

        InsuranceAgencyDTO insuranceAgencyPolicyHolderB = InsuranceAgencyDTO.builder()
                .name("Insurance Agency B")
                .address("890 Insurance Ave")
                .country("Belgium")
                .phoneNumber("2233445566")
                .email("agencyB@example.com")
                .build();

        InsuranceCompanyDTO insuranceCompanyPolicyHolderB = InsuranceCompanyDTO.builder()
                .name("Insurance Company B")
                .build();

        InsuranceCertificateDTO insuranceCertificatePolicyHolderBMotor = InsuranceCertificateDTO.builder()
                .policyNumber("PO198489552")
                .greenCardNumber("GC9874469")
                .availabilityDate(LocalDate.parse("2025-02-02"))
                .expirationDate(LocalDate.parse("2026-02-02"))
                .insuranceAgency(insuranceAgencyPolicyHolderB)
                .materialDamageCovered(false)
                .insuranceCompany(insuranceCompanyPolicyHolderB)
                .vehicle(motorB)
                .build();

        PolicyHolderDTO policyHolderVehicleB = PolicyHolderDTO.builder()
                .firstName("Policy B")
                .lastName("Holder B")
                .email("policy.holderB@example.com")
                .address("456 Policy St")
                .postalCode("67890")
                .phoneNumber("0987654321")
                .insuranceCertificates(new ArrayList<>(List.of(insuranceCertificatePolicyHolderBMotor)))
                .build();

        accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11").atStartOfDay())
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .witness(witness)
                .unregisteredTrailers(List.of(unregisteredTrailerB))
                .policyHolders(List.of(policyHolderVehicleA, policyHolderVehicleB))
                .vehicleACircumstances(List.of("PARKED/STOPPED"))
                .vehicleAInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleAVisibleDamageDescription("Visible Damage A")
                .vehicleAAccidentImages(accidentImagesVehicleA)
                .vehicleARemark("Remark A")
                .vehicleASignature(new byte[]{(byte) 1})
                .vehicleBCircumstances(List.of("PARKED/STOPPED"))
                .vehicleBInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleBVisibleDamageDescription("Visible Damage B")
                .vehicleBAccidentImages(accidentImagesVehicleB)
                .vehicleBRemark("Remark A")
                .vehicleBSignature(new byte[]{(byte) 1})
                .build();

        locationCoordinates = LocationCoordinates.builder().latitude(26.0).longitude(56.0).build();
    }

    @Test
    public void createStatementWithValidRequestReturnsCreated() throws Exception {
        // Arrange
        String expectedMessage = "Statement created successfully";
        when(accidentStatementService.createAccidentStatement(any(AccidentStatementDTO.class))).thenReturn(expectedMessage);

        // Act and Assert
        mockMvc.perform(post("/api/v1/statement/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accidentStatement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.successMessage", is(expectedMessage)));

        verify(accidentStatementService, times(1)).createAccidentStatement(any(AccidentStatementDTO.class));
    }

    @Test
    public void createStatementWithServiceErrorReturnsConflict() throws Exception {
        // Arrange
        when(accidentStatementService.createAccidentStatement(any(AccidentStatementDTO.class))).thenThrow(new Exception("Conflict"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/statement/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accidentStatement)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage", is("Conflict")));

        verify(accidentStatementService, times(1)).createAccidentStatement(any(AccidentStatementDTO.class));
    }

    @Test
    public void getAccidentLocationWithValidRequestReturnsOk() throws Exception {
        // Arrange
        String expectedMessage = "Location found";
        when(accidentStatementService.getLocationAddress(any(LocationCoordinates.class))).thenReturn(expectedMessage);

        // Act and Assert
        mockMvc.perform(post("/api/v1/statement/accident/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationCoordinates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successMessage", is(expectedMessage)));

        verify(accidentStatementService, times(1)).getLocationAddress(any(LocationCoordinates.class));
    }

    @Test
    public void getAccidentLocationWithServiceErrorReturnsInternalServerError() throws Exception {
        // Arrange
        when(accidentStatementService.getLocationAddress(any(LocationCoordinates.class))).thenThrow(new Exception("Internal server error"));

        // Act and Assert
        mockMvc.perform(post("/api/v1/statement/accident/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationCoordinates)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorMessage", is("Internal server error")));

        verify(accidentStatementService, times(1)).getLocationAddress(any(LocationCoordinates.class));
    }
}
