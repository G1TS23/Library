package org.library.service;

import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.dto.PagedResponse;
import org.library.entity.Book;
import org.library.repository.BookRepository;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void shouldCreateBook() {
        BookRequest request = new BookRequest("Clean Code", "Robert Martin", 2008);
        BookResponse response = service.create(request);
        assertEquals("Clean Code", response.title);
        assertEquals("Robert Martin", response.author);
        assertEquals(2008, response.year);
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockRepo).persist(bookCaptor.capture());
        assertEquals("Clean Code", bookCaptor.getValue().getTitle());
        assertEquals("Robert Martin", bookCaptor.getValue().getAuthor());
        assertEquals(2008, bookCaptor.getValue().getYear());
    }

    @Test
    void shouldReturnBookById() {
        Book book = new Book();
        book.setTitle("Clean Code");
        when(mockRepo.findByIdOptional(1L)).thenReturn(Optional.of(book));
        BookResponse result = service.findById(1L);
        assertEquals("Clean Code", result.title);
    }

    @Test
    void shouldThrowNotFoundWhenBookDoesNotExist() {
        when(mockRepo.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldReturnBooksFromOpenLibrary(){
        when(mockOpenLibraryService
                .searchByTitle("Clean Code", 0, 20))
                .thenReturn(new PagedResponse(1, 0, 20, List.of(new BookResponse("Clean Code", "Robert Martin", 2008))));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(1, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertEquals(1, result.items.size());
        assertEquals("Clean Code", result.items.getFirst().title);
        assertEquals("Robert Martin", result.items.getFirst().author);
        assertEquals(2008, result.items.getFirst().year);
    }

    @Test
    void shouldReturnEmptyListFromOpenLibrary(){
        when(mockOpenLibraryService
                .searchByTitle("Clean Code", 0, 20))
                .thenReturn(new PagedResponse(0, 0, 20, List.of()));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(0, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertTrue(result.items.isEmpty());
    }
}
