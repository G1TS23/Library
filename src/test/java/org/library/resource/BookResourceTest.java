package org.library.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldReturn400WhenSearchBookWithOffsetNegative(){
        given()
                .queryParam("offset", -1)
                .queryParam("limit", 20)
                .when().get("/books/search")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenSearchBookWithLimitNotPositive(){
        given()
                .queryParam("offset", 0)
                .queryParam("limit", 0)
                .when().get("/books/search")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldReturn400WhenSearchBookWithLimitOverFifty(){
        given()
                .queryParam("offset", 0)
                .queryParam("limit", 51)
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
}
