package org.library.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.library.dto.BookResponse;
import org.library.entity.Book;
import org.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class BookServiceTest {

    private static BookService service;
    private static BookRepository mockRepo;

    @BeforeAll
    static void setUp() {
        mockRepo = mock(BookRepository.class);
        service = new BookService(mockRepo, 5);
    }

    @Test
    void shouldReturnAllBooks() {
        Book book = new Book();
        book.setTitle("Clean Code");
        when(mockRepo.listAll()).thenReturn(List.of(book));

        List<BookResponse> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("Clean Code", result.getFirst().title);
    }

    @Test
    void shouldThrowNotFoundWhenBookDoesNotExist() {
        when(mockRepo.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

}
