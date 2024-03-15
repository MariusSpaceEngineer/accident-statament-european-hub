package com.inetum.realdolmen.hubkitbackend.integration;

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

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
public class AccidentStatementIntegrationTest {

    private RequestSpecification requestSpec;


    @BeforeAll
    public static void setUpClass() {
        baseURI = "https://localhost:8080/api/v1";
    }

    @BeforeEach
    public void setUpRequestSpecifications() {

        requestSpec = given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .contentType(ContentType.JSON);
    }


    @Test
    public void createStatementTest() {
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"1LOI958\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
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

    // JSON body with a driver that has an empty driving license number
    @Test
    public void createStatementWithNullDrivingLicenseNumberTest() {
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"ABC123\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullWitnessNameTest() {
        // JSON body with a witness that has an empty witness name
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"ABC123\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullWitnessAddressTest() {
        // JSON body with a witness that has an empty witness address
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"witness name\",\n" +
                "      \"address\": \"\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"ABC123\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullMotorLicensePlateTest() {
        // JSON body with a motor that has an empty motor license plate
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullTrailerLicensePlateTest() {
        // JSON body with a motor that has an empty trailer license plate
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"ABC123\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"Green123\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullPolicyHolderGreenCardNumberTest() {
        // JSON body with a policy holder that has an empty green card number
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"1LOI958\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"Policy123\",\n" +
                "        \"greenCardNumber\": \"\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void createStatementWithNullPolicyHolderPolicyNumberTest() {
        // JSON body with a policy holder that has an empty policy number
        String jsonBody = "{\n" +
                "  \"date\": \"2024-03-11\",\n" +
                "  \"location\": \"Brussels\",\n" +
                "  \"injured\": false,\n" +
                "  \"damageToOtherCars\": true,\n" +
                "  \"damageToObjects\": false,\n" +
                "  \"numberOfCircumstances\": 2,\n" +
                "  \"sketchOfImage\": 1,\n" +
                "  \"initialImpactVehicleA\": 1,\n" +
                "  \"initialImpactVehicleB\": 1,\n" +
                "  \"remarkVehicleA\": \"Remark A\",\n" +
                "  \"remarkVehicleB\": \"Remark B\",\n" +
                "  \"visibleDamageVehicleA\": \"Visible Damage A\",\n" +
                "  \"visibleDamageVehicleB\": \"Visible Damage B\",\n" +
                "  \"signatureVehicleA\": 1,\n" +
                "  \"signatureVehicleB\": 2,\n" +
                "  \"drivers\": [\n" +
                "    {\n" +
                "      \"firstName\": \"John\",\n" +
                "      \"lastName\": \"Doe\",\n" +
                "      \"birthday\": \"1980-01-01\",\n" +
                "      \"address\": \"123 Main St\",\n" +
                "      \"country\": \"Belgium\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"drivingLicenseNr\": \"123456\",\n" +
                "      \"category\": \"B\",\n" +
                "      \"drivingLicenseExpirationDate\": \"2030-01-01\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"witnesses\": [\n" +
                "    {\n" +
                "      \"name\": \"Witness Name\",\n" +
                "      \"address\": \"456 Witness St\",\n" +
                "      \"phoneNumber\": \"0987654321\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"motors\": [\n" +
                "    {\n" +
                "      \"brand\": \"Brand\",\n" +
                "      \"type\": \"Type\",\n" +
                "      \"licensePlate\": \"1LOI958\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"trailers\": [\n" +
                "    {\n" +
                "      \"licensePlate\": \"DEF456\",\n" +
                "      \"countryOfRegistration\": \"Belgium\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"policyHolders\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"firstName\": \"Policy\",\n" +
                "      \"lastName\": \"Holder\",\n" +
                "      \"email\": \"policy.holder@example.com\",\n" +
                "      \"address\": \"123 Policy St\",\n" +
                "      \"postalCode\": \"12345\",\n" +
                "      \"phoneNumber\": \"1234567890\",\n" +
                "      \"insuranceCertificate\": {\n" +
                "        \"policyNumber\": \"\",\n" +
                "        \"greenCardNumber\": \"465468\",\n" +
                "        \"availabilityDate\": \"2024-01-01\",\n" +
                "        \"expirationDate\": \"2025-01-01\",\n" +
                "        \"insuranceAgency\": {\n" +
                "          \"name\": \"Insurance Agency\",\n" +
                "          \"address\": \"789 Insurance Ave\",\n" +
                "          \"country\": \"Belgium\",\n" +
                "          \"phoneNumber\": \"1122334455\",\n" +
                "          \"email\": \"agency@example.com\"\n" +
                "        },\n" +
                "        \"insuranceCompany\": {\n" +
                "          \"name\": \"Insurance Company\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        requestSpec.body(jsonBody)
                .post("/statement/create")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }


}
