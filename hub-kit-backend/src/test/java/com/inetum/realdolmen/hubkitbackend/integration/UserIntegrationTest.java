package com.inetum.realdolmen.hubkitbackend.integration;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class UserIntegrationTest {
    private RequestSpecification requestSpec;

    @BeforeAll
    public static void setUpClass() {
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
    public void updatePolicyHolderPersonalInformationWithValidToken() {

        String jsonBody = "{\"firstName\": \"John john\", \"lastName\": \"Doe doe\"}";


        requestSpec
                .body(jsonBody)
                .when()
                .put(baseURI + "/user/profile/personal")
                .then()
                .statusCode(200);
    }

    @Test
    public void updatePolicyHolderPersonalInformationWithoutValidToken() {
        String jsonBody = "{\"firstName\": \"John john\", \"lastName\": \"Doe doe\"}";

        given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(jsonBody)
                .when().put(baseURI + "/user/profile/personal").then()
                .statusCode(403);
    }

    @Test
    public void updatePolicyHolderInsuranceInformationWithValidToken() {

        String jsonBody = "{\"policyNumber\": \"P56464894949546654\", \"greenCardNumber\": \"GCN987654321\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .put(baseURI + "/user/profile/insurance")
                .then()
                .statusCode(200);
    }

    @Test
    public void updatePolicyHolderInsuranceInformationWithoutValidToken() {
        String jsonBody = "{\"policyNumber\": \"P56464894949546654\", \"greenCardNumber\": \"GCN987654321\"}";

        given()
                .relaxedHTTPSValidation()
                .contentType("application/json")
                .body(jsonBody)
                .when().put(baseURI + "/user/profile/insurance").then()
                .statusCode(403);
    }

}