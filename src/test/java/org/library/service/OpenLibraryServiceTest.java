package org.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.client.OpenLibraryClient;
import org.library.dto.BookResponse;
import org.library.dto.openlibrary.OpenLibraryDoc;
import org.library.dto.openlibrary.OpenLibraryResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenLibraryServiceTest {

    private OpenLibraryService service;
    private OpenLibraryClient mockClient;
    private static final List<String> MOCK_FIELDS = List.of("title", "author_name", "first_publish_year");

    @BeforeEach
    void setup(){
        mockClient = mock(OpenLibraryClient.class);
        service = new OpenLibraryService(mockClient);
    }

    @Test
    void shouldRequestOnlyNeededFields(){
        when(mockClient.searchByTitle(eq("Clean Code"), any()))
                .thenReturn(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), 2008))));

        service.searchByTitle("Clean Code");

        verify(mockClient).searchByTitle("Clean Code", MOCK_FIELDS);
    }

    @Test
    void shouldReturnBooksFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any()))
                .thenReturn(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), 2008))));
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.getFirst().title);
        assertEquals("Robert Martin", result.getFirst().author);
        assertEquals(2008, result.getFirst().year);
    }

    @Test
    void shouldReturnBooksWithoutAuthorFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any()))
                .thenReturn(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", null, 2008))));
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.getFirst().title);
        assertNull(result.getFirst().author);
        assertEquals(2008, result.getFirst().year);
    }

    @Test
    void shouldReturnBooksWithoutYearFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any()))
                .thenReturn(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), null))));
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertEquals(1, result.size());
        assertEquals("Clean Code", result.getFirst().title);
        assertEquals("Robert Martin", result.getFirst().author);
        assertNull(result.getFirst().year);
    }

    @Test
    void shouldReturnEmptyListFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any()))
                .thenReturn(new OpenLibraryResponse(0, List.of()));
        List<BookResponse> result = service.searchByTitle("Clean Code");
        assertTrue(result.isEmpty());
    }

}
