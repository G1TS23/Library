package org.library.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenLibraryResponse {
    @JsonProperty("numFound")
    public int numFound;

    @JsonProperty("docs")
    public List<OpenLibraryDoc> docs;

    public OpenLibraryResponse() {}

    public OpenLibraryResponse(int numFound, List<OpenLibraryDoc> docs) {
        this.numFound = numFound;
        this.docs = docs;
    }
}
