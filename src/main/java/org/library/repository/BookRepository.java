package org.library.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.library.entity.Book;
import java.util.List;
import java.util.Optional;

/**
 * Panache repository for book data access.
 */
@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {

    /**
     * Finds all books by a given author.
     *
     * @param author name of the author
     * @return list of matching books
     */
    public List<Book> findByAuthor(String author) {
        return find("author", author).list();
    }

    /**
     * Finds a book by its exact title.
     *
     * @param title title of the book
     * @return an {@link Optional} containing the book if found
     */
    public Optional<Book> findByTitle(String title) {
        return find("title", title).firstResultOptional();
    }

    /**
     * Finds all books published in a given year.
     *
     * @param year publication year
     * @return list of matching books
     */
    public List<Book> findByYear(int year) {
        return find("year", year).list();
    }
}
