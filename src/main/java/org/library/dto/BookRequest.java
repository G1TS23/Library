package org.library.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO representing the data required to create a book.
 */
@Schema(description = "Données pour créer un livre")
public class BookRequest {
    /** Title of the book. */
    @Schema(description = "Titre du livre", examples = {"Clean Code"}, required = true)
    public String title;

    /** Author of the book. */
    @Schema(description = "Auteur du livre", examples = {"Robert Martin"}, required = true)
    public String author;

    /** Publication year of the book. */
    @Schema(description = "Année de publication", examples = {"2008"}, required = true)
    public int year;

    /** Default constructor required for JSON deserialization. */
    public BookRequest() {
    }

    /**
     * @param title  title of the book
     * @param author author of the book
     * @param year   publication year
     */
    public BookRequest(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
