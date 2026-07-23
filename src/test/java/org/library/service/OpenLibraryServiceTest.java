package org.library.service;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.client.OpenLibraryClient;
import org.library.dto.PagedResponse;
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
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), 2008)))));

        service.searchByTitle("Clean Code", 0, 20);

        verify(mockClient).searchByTitle("Clean Code", MOCK_FIELDS, 0, 20);
    }

    @Test
    void shouldReturnBooksFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), 2008)))));
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
    void shouldReturnBooksWithAuthorNullFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", null, 2008)))));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(1, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertEquals(1, result.items.size());
        assertEquals("Clean Code", result.items.getFirst().title);
        assertNull(result.items.getFirst().author);
        assertEquals(2008, result.items.getFirst().year);
    }

    @Test
    void shouldReturnBooksWithAuthorEmptyFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of(), 2008)))));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(1, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertEquals(1, result.items.size());
        assertEquals("Clean Code", result.items.getFirst().title);
        assertNull(result.items.getFirst().author);
        assertEquals(2008, result.items.getFirst().year);
    }

    @Test
    void shouldReturnBooksWithoutYearFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(1, List.of(new OpenLibraryDoc("Clean Code", List.of("Robert Martin"), null)))));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(1, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertEquals(1, result.items.size());
        assertEquals("Clean Code", result.items.getFirst().title);
        assertEquals("Robert Martin", result.items.getFirst().author);
        assertNull(result.items.getFirst().year);
    }

    @Test
    void shouldReturnEmptyListFromOpenLibrary(){
        when(mockClient.searchByTitle(eq("Clean Code"), any(), eq(0), eq(20)))
                .thenReturn(Uni.createFrom().item(new OpenLibraryResponse(0, List.of())));
        PagedResponse result = service.searchByTitle("Clean Code", 0, 20);
        assertEquals(0, result.total);
        assertEquals(0, result.offset);
        assertEquals(20, result.limit);
        assertTrue(result.items.isEmpty());
    }
}
