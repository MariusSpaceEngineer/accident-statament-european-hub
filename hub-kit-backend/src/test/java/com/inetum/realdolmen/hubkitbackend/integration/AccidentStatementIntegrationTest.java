package com.inetum.realdolmen.hubkitbackend.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inetum.realdolmen.hubkitbackend.dto.*;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
public class AccidentStatementIntegrationTest {
    private static ObjectMapper objectMapper;

    private RequestSpecification requestSpec;

    private WitnessDTO witness;

    private DriverDTO driverA;
    private MotorDTO motorA;
    private TrailerDTO trailerA;
    private PolicyHolderDTO policyHolderVehicleA;
    private List<AccidentImageDTO> accidentImagesVehicleA;

    private DriverDTO driverB;
    private MotorDTO motorB;
    private TrailerDTO trailerB;
    private PolicyHolderDTO policyHolderVehicleB;
    private List<AccidentImageDTO> accidentImagesVehicleB;


    @BeforeAll
    public static void setUpClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        baseURI = "https://localhost:8080/api/v1";
    }

    @BeforeEach
    public void setUpRequestSpecifications() {
        driverA = DriverDTO.builder()
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

        driverB = DriverDTO.builder()
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

        accidentImagesVehicleA = Collections.singletonList(AccidentImageDTO.builder().data(new byte[123]).build());
        accidentImagesVehicleB = Collections.singletonList(AccidentImageDTO.builder().data(new byte[123]).build());

        witness = WitnessDTO.builder()
                .name("Witness Name")
                .address("456 Witness St")
                .phoneNumber("0987654321")
                .build();

        motorA = MotorDTO.builder()
                .markType("Brand")
                .licensePlate("1LOI958")
                .countryOfRegistration("Belgium")
                .build();

        motorB = MotorDTO.builder()
                .markType("BrandB")
                .licensePlate("2LOI958")
                .countryOfRegistration("Belgium")
                .build();

        trailerA = TrailerDTO.builder()
                .hasRegistration(true)
                .licensePlate("DEF456")
                .countryOfRegistration("Belgium")
                .build();

        trailerB = TrailerDTO.builder()
                .hasRegistration(true)
                .licensePlate("GHI789")
                .countryOfRegistration("Belgium")
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

        InsuranceCertificateDTO insuranceCertificatePolicyHolderA = InsuranceCertificateDTO.builder()
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .insuranceAgency(insuranceAgencyPolicyHolderA)
                .insuranceCompany(insuranceCompanyPolicyHolderA)
                .build();

        policyHolderVehicleA = PolicyHolderDTO.builder()
                .id(1)
                .firstName("Policy")
                .lastName("Holder")
                .email("policy.holder@example.com")
                .address("123 Policy St")
                .postalCode("12345")
                .phoneNumber("1234567890")
                .insuranceCertificates(Collections.singletonList(insuranceCertificatePolicyHolderA))
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

        InsuranceCertificateDTO insuranceCertificatePolicyHolderB = InsuranceCertificateDTO.builder()
                .policyNumber("Policy456")
                .greenCardNumber("Green456")
                .availabilityDate(LocalDate.parse("2025-02-02"))
                .expirationDate(LocalDate.parse("2026-02-02"))
                .insuranceAgency(insuranceAgencyPolicyHolderB)
                .insuranceCompany(insuranceCompanyPolicyHolderB)
                .build();

        policyHolderVehicleB = PolicyHolderDTO.builder()
                .id(2)
                .firstName("Policy B")
                .lastName("Holder B")
                .email("policy.holderB@example.com")
                .address("456 Policy St")
                .postalCode("67890")
                .phoneNumber("0987654321")
                .insuranceCertificates(Collections.singletonList(insuranceCertificatePolicyHolderB))
                .build();
        requestSpec = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .contentType(ContentType.JSON);
    }

    //TODO fix test
    @Test
    public void createStatementTest() throws JsonProcessingException {

        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .witness(witness)
                .motors(List.of(motorA, motorB))
                .trailers(List.of(trailerA, trailerB))
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

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }

    @Test
    public void createStatementWithNullBodyTest() {
        String jsonBody = "{\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNoMotor() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .witness(witness)
                .trailers(List.of(trailerA, trailerB))
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

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void createStatementWithNoTrailer() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .witness(witness)
                .motors(List.of(motorA, motorB))
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

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void createStatementWithNoWitness() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .motors(List.of(motorA, motorB))
                .trailers(List.of(trailerA, trailerB))
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

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void createStatementWithNoAccidentImages() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(2)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .motors(List.of(motorA, motorB))
                .trailers(List.of(trailerA, trailerB))
                .policyHolders(List.of(policyHolderVehicleA, policyHolderVehicleB))
                .vehicleACircumstances(List.of("PARKED/STOPPED"))
                .vehicleAInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleAVisibleDamageDescription("Visible Damage A")
                .vehicleARemark("Remark A")
                .vehicleASignature(new byte[]{(byte) 1})
                .vehicleBCircumstances(List.of("PARKED/STOPPED"))
                .vehicleBInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleBVisibleDamageDescription("Visible Damage B")
                .vehicleBRemark("Remark A")
                .vehicleBSignature(new byte[]{(byte) 1})
                .build();

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void createStatementWithNoCircumstances() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(0)
                .sketchOfAccident(new byte[]{(byte) 1})
                .drivers(List.of(driverA, driverB))
                .motors(List.of(motorA, motorB))
                .trailers(List.of(trailerA, trailerB))
                .policyHolders(List.of(policyHolderVehicleA, policyHolderVehicleB))
                .vehicleAInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleAVisibleDamageDescription("Visible Damage A")
                .vehicleAAccidentImages(accidentImagesVehicleA)
                .vehicleARemark("Remark A")
                .vehicleASignature(new byte[]{(byte) 1})
                .vehicleBInitialImpactSketch(new byte[]{(byte) 1})
                .vehicleBVisibleDamageDescription("Visible Damage B")
                .vehicleBAccidentImages(accidentImagesVehicleB)
                .vehicleBRemark("Remark A")
                .vehicleBSignature(new byte[]{(byte) 1})
                .build();

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    public void createStatementWithNoSketches() throws JsonProcessingException {
        AccidentStatementDTO accidentStatement = AccidentStatementDTO.builder()
                .date(LocalDate.parse("2024-03-11"))
                .location("Brussels")
                .injured(false)
                .damageToOtherCars(true)
                .damageToObjects(false)
                .numberOfCircumstances(0)
                .drivers(List.of(driverA, driverB))
                .motors(List.of(motorA, motorB))
                .trailers(List.of(trailerA, trailerB))
                .policyHolders(List.of(policyHolderVehicleA, policyHolderVehicleB))
                .vehicleACircumstances(List.of("PARKED/STOPPED"))
                .vehicleAVisibleDamageDescription("Visible Damage A")
                .vehicleAAccidentImages(accidentImagesVehicleA)
                .vehicleARemark("Remark A")
                .vehicleASignature(new byte[]{(byte) 1})
                .vehicleBCircumstances(List.of("PARKED/STOPPED"))
                .vehicleBVisibleDamageDescription("Visible Damage B")
                .vehicleBAccidentImages(accidentImagesVehicleB)
                .vehicleBRemark("Remark A")
                .vehicleBSignature(new byte[]{(byte) 1})
                .build();

        String json = objectMapper.writeValueAsString(accidentStatement);

        requestSpec.body(json)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
