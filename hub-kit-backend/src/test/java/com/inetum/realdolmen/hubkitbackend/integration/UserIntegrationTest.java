package com.inetum.realdolmen.hubkitbackend.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceAgencyDTO;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCertificateDTO;
import com.inetum.realdolmen.hubkitbackend.dto.InsuranceCompanyDTO;
import com.inetum.realdolmen.hubkitbackend.dto.PolicyHolderPersonalInformationDTO;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class UserIntegrationTest {
    private static ObjectMapper objectMapper;


    private PolicyHolderPersonalInformationDTO policyHolderPersonalInformation;
    private InsuranceCertificateDTO insuranceCertificate;

    private RequestSpecification requestSpec;

    @BeforeAll
    public static void setUpClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        baseURI = "https://localhost:8080/api/v1";
    }

    @BeforeEach
    public void setUpRequestSpecifications() {
        String email = "johndoe@gmail.com";
        String password = "1234";

        Response response =
                given()
                        .relaxedHTTPSValidation()
                        .contentType("application/json")
                        .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
                        .when()
                        .post(baseURI + "/auth/login");

        String jwtToken = response.then()
                .statusCode(200)
                .extract()
                .path("token");

        policyHolderPersonalInformation = PolicyHolderPersonalInformationDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address("123 Main St")
                .postalCode("12345")
                .phoneNumber("123-456-7890")
                .build();

        InsuranceAgencyDTO insuranceAgency = InsuranceAgencyDTO.builder()
                .id(1)
                .name("Insurance Agency")
                .address("789 Insurance Ave")
                .country("Belgium")
                .phoneNumber("1122334455")
                .email("agency@example.com")
                .build();

        InsuranceCompanyDTO insuranceCompany = InsuranceCompanyDTO.builder()
                .id(1)
                .name("Insurance Company")
                .build();

        insuranceCertificate = InsuranceCertificateDTO.builder()
                .id(1)
                .policyNumber("Policy123")
                .greenCardNumber("Green123")
                .availabilityDate(LocalDate.parse("2024-01-01"))
                .expirationDate(LocalDate.parse("2025-01-01"))
                .insuranceAgency(insuranceAgency)
                .insuranceCompany(insuranceCompany)
                .build();


        requestSpec = given()
                .relaxedHTTPSValidation()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json");
    }

    @Test
    public void getPolicyHolderProfileWithAuthorizedToken() {
        requestSpec.when()
                .get(baseURI + "/user/profile")
                .then()
                .statusCode(200);

    }

    @Test
    public void getPolicyHolderProfileWithoutAuthorizedToken() {
        given()
                .relaxedHTTPSValidation()
                .header("Authorization", "Bearer 49849465")
                .contentType("application/json")
                .when().get(baseURI + "/user/profile").then()
                .statusCode(403);

    }

    @Test
    public void getPolicyHolderProfileWithInvalidAuthorizedToken() {
        given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .when().get(baseURI + "/user/profile").then()
                .statusCode(403);

    }

    @Test
    public void updatePolicyHolderPersonalInformationWithValidToken() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(policyHolderPersonalInformation);

        requestSpec
                .body(json)
                .when()
                .put(baseURI + "/user/profile/personal")
                .then()
                .statusCode(200);
    }


    @Test
    public void updatePolicyHolderPersonalInformationWithoutValidToken() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(policyHolderPersonalInformation);


        given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(json)
                .when().put(baseURI + "/user/profile/personal").then()
                .statusCode(403);
    }

    @Test
    public void updatePolicyHolderInsuranceInformationWithValidToken() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(insuranceCertificate);

        requestSpec
                .body(json)
                .when()
                .put(baseURI + "/user/profile/insurance")
                .then()
                .statusCode(200);
    }

    @Test
    public void updatePolicyHolderInsuranceInformationWithoutValidToken() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(insuranceCertificate);

        given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(json)
                .when().put(baseURI + "/user/profile/insurance").then()
                .statusCode(403);
    }

}