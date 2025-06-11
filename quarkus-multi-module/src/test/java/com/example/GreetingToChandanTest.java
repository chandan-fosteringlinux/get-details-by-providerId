package com.example;

import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
class GreetingToChandanTest {
    @Test
    void testHelloEndpoint() {
        given()
          .when().get("/helloChandan")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }
}