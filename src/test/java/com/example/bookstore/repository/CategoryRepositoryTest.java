package com.example.bookstore.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql(scripts = {
        "classpath:databases/category/01-insert-3-categories.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:databases/category/02-delete-categories.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void existsById_ReturnFalse() {
        Assertions.assertFalse(categoryRepository.existsById(-1L));
        Assertions.assertFalse(categoryRepository.existsById(100L));
    }

    @Test
    public void existsById_ReturnTrue() {
        Assertions.assertTrue(categoryRepository.existsById(1L));
        Assertions.assertTrue(categoryRepository.existsById(2L));
    }
}
