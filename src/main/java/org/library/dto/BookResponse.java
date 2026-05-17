package org.library.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.library.entity.Book;

/**
 * DTO representing book information returned by the API.
 */
@Schema(description = "Réponse contenant les informations d'un livre")
public class BookResponse {
    /** Title of the book. */
    @Schema(description = "Titre du livre", examples = {"Clean Code"})
    public String title;

    /** Author of the book. */
    @Schema(description = "Auteur du livre", examples = {"Robert Martin"})
    public String author;

    /** Publication year of the book. */
    @Schema(description = "Année de publication", examples = {"2008"})
    public int year;

    /** Default constructor required for JSON deserialization. */
    public BookResponse() {
    }

    /**
     * @param title  title of the book
     * @param author author of the book
     * @param year   publication year
     */
    public BookResponse(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    /**
     * Converts a {@link Book} entity into a {@code BookResponse}.
     *
     * @param book the source entity
     * @return the corresponding DTO
     */
    public static BookResponse from(Book book) {
        BookResponse dto = new BookResponse();
        dto.title = book.getTitle();
        dto.author = book.getAuthor();
        dto.year = book.getYear();
        return dto;
    }
}
