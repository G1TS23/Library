package org.library.repository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.library.entity.Book;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test d'intégration — nécessite @QuarkusTest + Dev Services
@QuarkusTest
class BookRepositoryTest {

    @Inject
    BookRepository bookRepository;

    @Test
    @TestTransaction
    void shouldFindByAuthor() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        List<Book> result = bookRepository.findByAuthor("Robert Martin");
        assertEquals(1, result.size());
    }

    @Test
    @TestTransaction
    void shouldFindNoBooksByAuthor() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        List<Book> result = bookRepository.findByAuthor("Stephen King");
        assertTrue(result.isEmpty());
    }

    @Test
    @TestTransaction
    void shouldFindByTitle() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        Optional<Book> result = bookRepository.findByTitle("Clean Code");

        assertTrue(result.isPresent());
        assertEquals("Clean Code", result.get().getTitle());
        assertEquals("Robert Martin", result.get().getAuthor());
        assertEquals(2008, result.get().getYear());
    }

    @Test
    @TestTransaction
    void shouldFindNoBooksByTitle() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        List<Book> result = bookRepository.findByAuthor("IT");
        assertTrue(result.isEmpty());
    }

    @Test
    @TestTransaction
    void shouldFindByYear() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        List<Book> result = bookRepository.findByYear(2008);
        assertEquals(1, result.size());
    }

    @Test
    @TestTransaction
    void shouldFindNoBooksByYear() {
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setAuthor("Robert Martin");
        book.setYear(2008);
        bookRepository.persist(book);

        List<Book> result = bookRepository.findByYear(1980);
        assertTrue(result.isEmpty());
    }

}
