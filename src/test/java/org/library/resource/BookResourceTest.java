package org.library.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.dto.PagedResponse;
import org.library.service.BookService;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestProfile(JwtTestProfile.class)
class BookResourceTest {

    @InjectMock
    BookService bookService;

    @Test
    void shouldReturn200WithBooks() {
        when(bookService.findAll()).thenReturn(List.of(
                new BookResponse("Clean Code", "Robert Martin", 2008)
        ));

        given()
                .when().get("/books")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].title", is("Clean Code"));
    }

    @Test
    void shouldReturn200WithBook() {
        when(bookService.findById(1L)).thenReturn(
                new BookResponse("Clean Code", "Robert Martin", 2008)
        );
        given()
                .when().get("/books/1")
                .then()
                .statusCode(200)
                .body("title", is("Clean Code"));
    }

    @Test
    void shouldReturn404WhenBookNotFound() {
        when(bookService.findById(99L))
                .thenThrow(new NotFoundException("Book not found"));

        given()
                .when().get("/books/99")
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn400WhenBookCreatedWithNoTitleProvided() {
        BookRequest request = new BookRequest();
        request.author = "Robert Martin";
        request.year = 2008;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(400);
        verifyNoInteractions(bookService);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn400WhenBookCreatedWithYearZeroProvided() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.author = "Robert Martin";
        request.year = 0;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(400);
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturn401WhenBookCreatedWithoutTokenProvided() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.author = "Robert Martin";
        request.year = 2008;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(401);
        verifyNoInteractions(bookService);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    @JwtSecurity(claims = { @Claim(key = "email", value = "user@library.com") })
    void shouldReturn403WhenBookCreatedWithUserRole() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.author = "Robert Martin";
        request.year = 2008;

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(403);
        verifyNoInteractions(bookService);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn201WhenBookCreatedWithNoAuthorProvided() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.year = 2008;

        when(bookService.create(any())).thenReturn(
                new BookResponse("Clean Code", null, 2008)
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .body("title", is("Clean Code"))
                .body("author", nullValue())
                .body("year", is(2008));
    }

    @Test
    void shouldReturn201WhenBookCreatedWithForgedTokenProvided() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.author = "Robert Martin";
        request.year = 2008;

        when(bookService.create(any())).thenReturn(
                new BookResponse("Clean Code", "Robert Martin", 2008)
        );

        String token = Jwt.claims().issuer("https://example.com/issuer")
                .upn("admin@library.com")
                .groups("ADMIN")
                .sign(JwtTestProfile.PRIVATE_KEY);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/books")
                .then()
                .statusCode(201)
                .body("title", is("Clean Code"))
                .body("author", is("Robert Martin"))
                .body("year", is(2008));
    }

    @Test
    void shouldReturn401WhenBookCreatedWithForgedTokenProvidedWithWrongIssuer() {
        BookRequest request = new BookRequest();
        request.title = "Clean Code";
        request.author = "Robert Martin";
        request.year = 2008;

        when(bookService.create(any())).thenReturn(
                new BookResponse("Clean Code", "Robert Martin", 2008)
        );

        String token = Jwt.claims().issuer("https://forged-issuer.com")
                .upn("admin@library.com")
                .groups("ADMIN")
                .sign(JwtTestProfile.PRIVATE_KEY);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/books")
                .then()
                .statusCode(401);
        verifyNoInteractions(bookService);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn201WhenBookCreated() {
        BookRequest request = new BookRequest("Clean Code", "Robert Martin", 2008);
        when(bookService.create(any())).thenReturn(
                new BookResponse("Clean Code", "Robert Martin", 2008)
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/books")
                .then()
                .statusCode(201);
    }

    @Test
    void shouldReturn200WhenSearchBookFromOpenLibrary(){
        when(bookService.searchByTitle("Clean Code", 0, 20))
                .thenReturn(new PagedResponse(1, 0, 20, List.of(new BookResponse("Clean Code", "Robert Martin", 2008))));
        given()
                .queryParam("title", "Clean Code")
                .queryParam("offset", 0)
                .queryParam("limit", 20)
                .when()
                .get("/books/search")
                .then()
                .statusCode(200)
                .body("total", is(1))
                .body("offset", is(0))
                .body("limit", is(20))
                .body("items.size()", is(1))
                .body("items[0].title", is("Clean Code"));
    }

    @Test
    void shouldReturn200WhenSearchBookWithoutTitleFromOpenLibrary(){
        when(bookService.searchByTitle(null, 0, 20))
                .thenReturn(new PagedResponse(0, 0, 20, List.of()));
        given()
                .queryParam("offset", 0)
                .queryParam("limit", 20)
                .when().get("/books/search")
                .then()
                .statusCode(200)
                .body("items.size()", is(0));
    }

    @ParameterizedTest(name = "offset={0}, limit={1}")
    @CsvSource({
        "-1,20",
        "0,0",
        "0,51"
    })
    void shouldReturn400WhenSearchBookWithOutOfBoundsLimitOrOffset(int offset, int limit){
        given()
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .when().get("/books/search")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldHaveDefaultValueOnOffsetAndLimitWhenSearchBookWithoutQueryParams(){
        when(bookService.searchByTitle(eq("Clean Code"), any(), any()))
                .thenReturn(new PagedResponse(1, 0, 20, List.of(new BookResponse("Clean Code", "Robert Martin", 2008))));
        given()
                .queryParam("title", "Clean Code")
                .when().get("/books/search")
                .then()
                .statusCode(200)
                .body("offset", is(0))
                .body("limit", is(20))
                .body("items.size()", is(1))
                .body("items[0].title", is("Clean Code"));
        verify(bookService).searchByTitle("Clean Code", 0, 20);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn204WhenBookDeleted() {
        when(bookService.deleteById(1L)).thenReturn(true);
        given()
                .pathParam("id", 1L)
                .when().delete("/books/{id}")
                .then()
                .statusCode(204);
        verify(bookService).deleteById(1L);
    }

    @Test
    @TestSecurity(user = "admin", roles = "ADMIN")
    @JwtSecurity(claims = { @Claim(key = "email", value = "admin@library.com") })
    void shouldReturn404WhenBookDeletedWithWrongId() {
        when(bookService.deleteById(3L)).thenReturn(false);
        given()
                .pathParam("id", 3L)
                .when().delete("/books/{id}")
                .then()
                .statusCode(404);
        verify(bookService).deleteById(3L);
    }

    @Test
    @TestSecurity(user = "user", roles = "USER")
    @JwtSecurity(claims = { @Claim(key = "email", value = "user@library.com") })
    void shouldReturn403WhenBookDeletedWithoutProperAuthorization() {
        given()
                .pathParam("id", 1L)
                .when().delete("/books/{id}")
                .then()
                .statusCode(403);
        verifyNoInteractions(bookService);
    }

    @Test
    void shouldReturn401WhenBookDeletedWithoutTokenProvided() {
        given()
                .pathParam("id", 1L)
                .when().delete("/books/{id}")
                .then()
                .statusCode(401);
        verifyNoInteractions(bookService);
    }
}
