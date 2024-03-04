package com.inetum.realdolmen.hubkitbackend.integration;

import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void setUpClass() {
        baseURI = "https://localhost:8080/api/v1/auth";
    }

    private RequestSpecification requestSpec;

    @BeforeEach
    public void setUpRequestSpecifications() {
        requestSpec = given()
                .relaxedHTTPSValidation()
                .contentType("application/json");
    }

    @Test
    public void userLoginIsRight() {
        Optional<User> userOptional = userRepository.findByEmail("johndoe@gmail.com");

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            requestSpec
                    .body("{\"email\": \"" + user.getEmail() + "\", \"password\": 1234}")
                    .when()
                    .post(baseURI + "/login")
                    .then()
                    .statusCode(200)
                    .body("token", notNullValue())
                    .body("errorMessage", nullValue());
        }

    }

    @Test
    public void userLoginIsNotRight() {
        requestSpec
                .body("{\"email\": \"wrongEmail@gmail.com\", \"password\": 12345}")
                .when()
                .post(baseURI + "/login")
                .then()
                .statusCode(400)
                .body("token", nullValue())
                .body("errorMessage", notNullValue());
    }

    @Test
    public void createUserThatDoesntExists() {
        String jsonBody = "{\"email\": \"user2@gmail.com\", \"password\": \"1234\", \"firstName\": \"Jack\", " +
                "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .post(baseURI + "/register")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("errorMessage", nullValue());

    }

    @Test
    public void createUserThatExists() {
        Optional<User> userOptional = userRepository.findByEmail("johndoe@gmail.com");

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            requestSpec
                    .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \"1234\", \"firstName\": \"Jack\", " +
                            "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                            "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}")
                    .when()
                    .post(baseURI + "/register")
                    .then()
                    .statusCode(409)
                    .body("token", nullValue())
                    .body("errorMessage", notNullValue());

        }
    }

}