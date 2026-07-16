package org.library.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.service.BookService;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
}
