package com.inetum.realdolmen.hubkitbackend.integration;

import com.inetum.realdolmen.hubkitbackend.models.User;
import com.inetum.realdolmen.hubkitbackend.repositories.UserRepository;
import com.inetum.realdolmen.hubkitbackend.services.MailService;
import com.mailjet.client.errors.MailjetException;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    //TODO stop the email service from sending email in create user test
    @MockBean
    private MailService mailService;

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
                    .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \"" + user.getPassword() + "\"}")
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
                .body("{\"email\": \"wrongEmail@gmail.com\", \"password\": \"Example_123\"}")
                .when()
                .post(baseURI + "/login")
                .then()
                .statusCode(401)
                .body("token", nullValue())
                .body("errorMessage", notNullValue());
    }

    @Test
    @Order(3)
    public void userLoginHasWrongEmail() {
        requestSpec
                .body("{\"email\": \"wrongEmail@gmail...com\", \"password\": Example_123}")
                .when()
                .post(baseURI + "/login")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(4)
    public void userLoginHasMissingProperty() {
        requestSpec
                .body("{\"email\": \"\", \"password\": Example_123}")
                .when()
                .post(baseURI + "/login")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(5)
    public void createUserThatDoesntExists() throws MailjetException {
        String jsonBody = "{\"email\": \"user2@gmail.com\", \"password\": \"ExamplePass123_\", \"firstName\": \"Jack\", " +
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
    @Order(6)
    public void createUserWithWrongPassword() {
        String jsonBody = "{\"email\": \"user3@gmail.com\", \"password\": \"1234\", \"firstName\": \"Jack\", " +
                "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .post(baseURI + "/register")
                .then()
                .statusCode(400)
                .body("token", nullValue())
                .body("errorMessage", notNullValue());

    }

    @Test
    @Order(7)
    public void createUserWithWrongEmail() {
        String jsonBody = "{\"email\": \"user3@gmail...com\", \"password\": \"1234\", \"firstName\": \"Jack\", " +
                "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .post(baseURI + "/register")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(8)
    public void createUserWithMissingProperties() {
        String jsonBody = "{\"password\": \"1234\", \"firstName\": \"Jack\", " +
                "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .post(baseURI + "/register")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(9)
    public void createUserWithEmptyProperties() {
        String jsonBody = "{\"email\": \"\", \"password\": \"1234\", \"firstName\": \"Jack\", " +
                "\"lastName\": \"Sparrow\", \"address\": \"Koningin Astridplein 28, 2018 Antwerpen\", " +
                "\"postalCode\": \"2678\", \"phoneNumber\": \"0465879485\"}";

        requestSpec
                .body(jsonBody)
                .when()
                .post(baseURI + "/register")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(10)
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
    @Order(11)
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
    @Order(12)
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
    @Order(13)
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
    @Order(14)
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