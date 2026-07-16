package org.library.service;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.dto.BookResponse;
import org.library.entity.Book;
import org.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookService service;
    private BookRepository mockRepo;
    private OpenLibraryService mockOpenLibraryService;

    @BeforeEach
    void setUp() {
        mockRepo = mock(BookRepository.class);
        mockOpenLibraryService = mock(OpenLibraryService.class);
        service = new BookService(mockRepo, mockOpenLibraryService, 5);
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

    @Test
    void shouldReturnBooksFromOpenLibrary(){
        when(mockOpenLibraryService
                .searchByTitle("Clean Code"))
                .thenReturn(List.of(new BookResponse("Clean Code", "Robert Martin", 2008)));
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.getFirst().title);
        assertEquals("Robert Martin", result.getFirst().author);
        assertEquals(2008, result.getFirst().year);
    }

    @Test
    void shouldReturnEmptyListFromOpenLibrary(){
        when(mockOpenLibraryService
                .searchByTitle("Clean Code"))
                .thenReturn(List.of());
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertTrue(result.isEmpty());
    }


}
