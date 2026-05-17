package org.library.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing a book stored in the database.
 */
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_seq")
    @SequenceGenerator(name = "books_SEQ", sequenceName = "books_SEQ", allocationSize = 1)
    private Long id;
    private String title;
    private String author;
    private int year;

    /**
     * @return unique identifier of the book
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id unique identifier of the book
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title title of the book
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author author of the book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return publication year of the book
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year publication year of the book
     */
    public void setYear(int year) {
        this.year = year;
    }
}
