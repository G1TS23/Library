package org.library.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestProfile(OpenLibraryDownProfile.class)
class OpenLibrarySearchFallbackTest {

    @Test
    void shoudReturn503WhenOpenLibraryServiceIsUnavailable() {
        given()
                .queryParam("title", "Clean Code")
                .when().get("/books/search")
                .then()
                .statusCode(503)
                .header("Content-Length", "0")
                .header("Retry-After", "15");
    }

    @Test
    void shouldReturn400WhenOpenLibraryServiceIsUnavailableAndWrongQueryParams() {
        given()
                .queryParam("title", "Clean Code")
                .queryParam("offset", -1)
                .when().get("/books/search")
                .then()
                .statusCode(400);
    }
}
