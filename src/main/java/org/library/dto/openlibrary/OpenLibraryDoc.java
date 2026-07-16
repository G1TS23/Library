package org.library.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// dto/openLibrary/OpenLibraryDoc.java
public class OpenLibraryDoc {
    @JsonProperty("title")
    public String title;

    @JsonProperty("author_name")
    public List<String> authorName;

    @JsonProperty("first_publish_year")
    public Integer firstPublishYear;

    public OpenLibraryDoc() {}

    public OpenLibraryDoc(String title, List<String> authorName, Integer firstPublishYear) {
        this.title = title;
        this.authorName = authorName;
        this.firstPublishYear = firstPublishYear;
    }
}

