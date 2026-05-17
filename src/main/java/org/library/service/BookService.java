package org.library.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.entity.Book;
import org.library.repository.BookRepository;
import java.util.List;

/**
 * Application service handling business logic related to books.
 */
@ApplicationScoped
public class BookService {

    private final BookRepository bookRepository;

    /**
     * @param bookRepository repository for book data access
     */
    @Inject
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Returns all registered books.
     *
     * @return list of all books as DTOs
     */
    public List<BookResponse> findAll() {
        return bookRepository.listAll().stream()
                .map(BookResponse::from)
                .toList();
    }

    /**
     * Creates and persists a new book.
     *
     * @param request data of the book to create
     * @return the created book as a DTO
     */
    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.title);
        book.setAuthor(request.author);
        book.setYear(request.year);
        bookRepository.persist(book);
        return BookResponse.from(book);
    }

    /**
     * Finds a book by its identifier.
     *
     * @param id identifier of the book
     * @return the matching book as a DTO
     * @throws jakarta.ws.rs.NotFoundException if no book is found with that identifier
     */
    public BookResponse findById(Long id) {
        return bookRepository.findByIdOptional(id)
                .map(BookResponse::from)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    }
}
