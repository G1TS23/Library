package org.library.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.library.client.OpenLibraryClient;
import org.library.dto.BookResponse;
import org.library.dto.PagedResponse;
import org.library.dto.openlibrary.OpenLibraryDoc;
import org.library.dto.openlibrary.OpenLibraryResponse;

import java.util.List;
import java.util.Optional;

@ApplicationScoped()
public class OpenLibraryService {

    private final OpenLibraryClient client;

    private static final List<String> FIELDS = List.of("title", "author_name", "first_publish_year");

    @Inject
    public OpenLibraryService(@RestClient OpenLibraryClient client) {
        this.client = client;
    }

    private static PagedResponse from(OpenLibraryResponse openLibraryResponses) {
        PagedResponse pagedResponse = new PagedResponse();
        pagedResponse.total = openLibraryResponses.numFound;
        pagedResponse.items = openLibraryResponses.docs.stream()
                .map(doc -> new BookResponse(
                        doc.title,
                        getAuthor(doc),
                        doc.firstPublishYear))
                    .toList();
        return pagedResponse;
    }

    public PagedResponse searchByTitle(String title, Integer offset, Integer limit) {
        OpenLibraryResponse response = client.searchByTitle(title, FIELDS, offset, limit).await().indefinitely();
        PagedResponse pagedResponse = from(response);
        pagedResponse.limit = limit;
        pagedResponse.offset = offset;
        return pagedResponse;
    }

    private static String getAuthor(OpenLibraryDoc doc) {
        return Optional.ofNullable(doc.authorName)
                .filter(authors -> !authors.isEmpty())
                .map(List::getFirst)
                .orElse(null);
    }

}
