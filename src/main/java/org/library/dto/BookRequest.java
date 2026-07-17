package org.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO representing the data required to create a book.
 */
@Schema(description = "Données pour créer un livre")
public class BookRequest {
    /** Title of the book. */
    @Schema(description = "Titre du livre", examples = {"Clean Code"}, required = true)
    @NotBlank(message = "Le titre ne peut pas être vide")
    public String title;

    /** Author of the book. */
    @Schema(description = "Auteur du livre", examples = {"Robert Martin"})
    public String author;

    /** Publication year of the book. */
    @Schema(description = "Année de publication", examples = {"2008"}, required = true, minimum = "1")
    @Min(value = 1, message = "L'année de publication doit être un nombre positif")
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
