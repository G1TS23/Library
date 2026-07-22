package org.library.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.library.dto.BookRequest;
import org.library.dto.BookResponse;
import org.library.dto.PagedResponse;
import org.library.entity.Book;
import org.library.repository.BookRepository;
import java.util.List;

/**
 * Application service handling business logic related to books.
 */
@ApplicationScoped
public class BookService {

    private final BookRepository bookRepository;
    private final OpenLibraryService openLibraryService;

    private final int maxLoans;

    /**
     * @param bookRepository repository for book data access
     */
    @Inject
    public BookService(
            BookRepository bookRepository,
            OpenLibraryService openLibraryService,
            @ConfigProperty(name = "library.max-loans", defaultValue = "5") int maxLoans) {
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
        this.maxLoans = maxLoans;
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

    /**
     * Search for a book by its title
     * @param title of the book
     * @param offset number of results per page
     * @param limit maximum number of results to return
     * @return a PagedResponse containing the search results
     */
    public PagedResponse searchByTitle(String title, Integer offset, Integer limit) {
        return openLibraryService.searchByTitle(title, offset, limit);
    }

    /**
     * Deletes a book by its identifier.
     *
     * @param id identifier of the book to delete
     */
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
