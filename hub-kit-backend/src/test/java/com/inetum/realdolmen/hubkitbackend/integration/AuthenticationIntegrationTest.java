package com.inetum.realdolmen.hubkitbackend.integration;

import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
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
    @Order(2)
    public void userLoginIsNotRight() {
        requestSpec
                .body("{\"email\": \"wrongEmail@gmail.com\", \"password\": 12345}")
                .when()
                .post(baseURI + "/login")
                .then()
                .statusCode(401)
                .body("token", nullValue())
                .body("errorMessage", notNullValue());
    }

    @Test
    @Order(3)
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
    @Order(4)
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

    @Test
    @Order(5)
    public void updatePasswordWithValidCredentials() {
        String email = "johndoe@gmail.com"; // replace with a valid email in your test database
        String newPassword = "newPassword";
        String securityCode = "123456"; // replace with a valid security code

        requestSpec
                .body("{\"email\": \"" + email + "\", \"newPassword\": \"" + newPassword + "\", \"securityCode\": \"" + securityCode + "\"}")
                .when()
                .post(baseURI + "/reset/password")
                .then()
                .statusCode(200)
                .body("successMessage", equalTo("Password reset successful"))
                .body("errorMessage", nullValue());
    }

    @Test
    @Order(6)
    public void resetPasswordWithValidEmail() {
        String email = "johndoe@gmail.com"; // replace with a valid email in your test database

        requestSpec
                .body("{\"email\": \"" + email + "\"}")
                .when()
                .post(baseURI + "/reset")
                .then()
                .statusCode(200)
                .body("successMessage", equalTo("Password reset email sent successfully"))
                .body("errorMessage", nullValue());
    }


    @Test
    @Order(7)
    public void resetPasswordWithInvalidEmail() {
        String email = "invalidEmail@gmail.com"; // replace with an invalid email

        requestSpec
                .body("{\"email\": \"" + email + "\"}")
                .when()
                .post(baseURI + "/reset")
                .then()
                .statusCode(400)
                .body("successMessage", nullValue())
                .body("errorMessage", notNullValue());
    }

    @Test
    @Order(8)
    public void updatePasswordWithInvalidCredentials() {
        String email = "invalidEmail@gmail.com"; // replace with an invalid email
        String newPassword = "newPassword";
        String securityCode = "123456"; // replace with an invalid security code

        requestSpec
                .body("{\"email\": \"" + email + "\", \"newPassword\": \"" + newPassword + "\", \"securityCode\": \"" + securityCode + "\"}")
                .when()
                .post(baseURI + "/reset/password")
                .then()
                .statusCode(400)
                .body("successMessage", nullValue())
                .body("errorMessage", notNullValue());
    }


}