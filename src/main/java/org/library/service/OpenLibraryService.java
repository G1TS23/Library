package org.library.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.library.client.OpenLibraryClient;
import org.library.dto.BookResponse;
import org.library.dto.openlibrary.OpenLibraryDoc;
import org.library.dto.openlibrary.OpenLibraryResponse;

import java.util.List;
import java.util.Optional;

@ApplicationScoped()
public class OpenLibraryService {

    private final OpenLibraryClient client;

    @Inject
    public OpenLibraryService(@RestClient OpenLibraryClient client) {
        this.client = client;
    }

    private static List<BookResponse> from(OpenLibraryResponse openLibraryResponses) {
            return openLibraryResponses.docs.stream()
                    .map(doc -> new BookResponse(
                            doc.title,
                            getAuthor(doc),
                            doc.firstPublishYear))
                    .toList();
    }

    public List<BookResponse> searchByTitle(String title) {
        return from(client.searchByTitle(title));
    }

    private static String getAuthor(OpenLibraryDoc doc) {
        return Optional.ofNullable(doc.authorName)
                .filter(authors -> !authors.isEmpty())
                .map(List::getFirst)
                .orElse(null);
    }

}
