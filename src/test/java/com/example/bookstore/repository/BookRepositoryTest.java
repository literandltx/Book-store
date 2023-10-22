package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Category;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = {
        "classpath:databases/category/01-insert-3-categories.sql",
        "classpath:databases/book/01-insert-3-books.sql",
        "classpath:databases/book/02-set-books-categories.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:databases/book/04-delete-books-categories.sql",
        "classpath:databases/category/02-delete-categories.sql",
        "classpath:databases/book/03-delete-books.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void existsById_ReturnFalse() {
        Assertions.assertFalse(bookRepository.existsById(-1L));
        Assertions.assertFalse(bookRepository.existsById(100L));
    }

    @Test
    public void existsById_ReturnTrue() {
        Assertions.assertTrue(bookRepository.existsById(2L));
        Assertions.assertTrue(bookRepository.existsById(3L));
    }

    @Test
    @Sql(scripts = {
            "classpath:databases/book/03-delete-books.sql",
            "classpath:databases/book/01-insert-3-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:databases/book/03-delete-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAll_ReturnListOfThreeBooks() {
        long expectedSize = 3L;
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertEquals(expectedSize, bookRepository.findAll(pageable).getTotalElements());
    }

    @Test
    @Sql(scripts = {
            "classpath:databases/book/03-delete-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAll_ReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertTrue(bookRepository.findAll(pageable).isEmpty());
    }

    @Test
    public void findById_ReturnBook() {
        Book expected = new Book();
        expected.setId(3L);
        expected.setTitle("book1");
        expected.setIsbn("0000000000001");
        expected.setPrice(new BigDecimal("100.00"));
        expected.setAuthor("author1");

        Category category = new Category(1L);
        category.setName("Horror");

        expected.setCategories(Set.of(category));

        Optional<Book> actual = bookRepository.findById(3L);
        Assertions.assertEquals(expected, actual.orElse(new Book()));
    }

    @Test
    public void findById_ReturnNull() {
        Book expected = new Book(-1L);

        Assertions.assertEquals(expected, bookRepository.findById(100L).orElse(new Book(-1L)));
    }

    @Test
    public void findAllByCategoryId_ReturnListOfOneBook() {
        long expectedSize = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertEquals(expectedSize,
                bookRepository.findAllByCategoryId(2L, pageable).size());
    }

    @Test
    public void findAllByCategoryId_ReturnListOfTwoBooks() {
        long expectedSize = 3L;
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertEquals(expectedSize,
                bookRepository.findAllByCategoryId(1L, pageable).size());
    }

    @Test
    public void findAllByCategoryId_ReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertTrue(bookRepository.findAllByCategoryId(100L, pageable).isEmpty());
    }
}
