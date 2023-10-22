package com.example.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("databases/book/01-insert-3-books.sql")
            );

        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Sql(
            scripts = "classpath:databases/book/05-delete-book-by-isn.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createBook_Success() throws Exception {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setIsbn("1111111111");
        bookRequestDto.setPrice(BigDecimal.valueOf(100));
        bookRequestDto.setTitle("Book1");
        bookRequestDto.setAuthor("Author test");
        bookRequestDto.setCategoryIds(new HashSet<>());

        BookDto expected = new BookDto();
        expected.setIsbn(bookRequestDto.getIsbn());
        expected.setAuthor(bookRequestDto.getAuthor());
        expected.setTitle(bookRequestDto.getTitle());

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @WithMockUser(username = "user")
    void getBookById_Success() throws Exception {
        // Given
        Long bookId = 3L;
        BookDto expected = new BookDto();
        expected.setId(bookId);
        expected.setAuthor("author1");
        expected.setTitle("book1");
        expected.setIsbn("0000000000001");
        expected.setPrice(new BigDecimal("100.00"));
        expected.setCategoryIds(new HashSet<>());

        // When
        MvcResult result = mockMvc.perform(get("/api/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        BookDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    void findAll_Success() throws Exception {
        // Given
        int expectedSize = 3;
        // When
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        // Then
        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        assertEquals(expectedSize, actual.length);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBook_Success() throws Exception {
        // Given
        CreateBookRequestDto updatedBook = new CreateBookRequestDto();
        updatedBook.setPrice(new BigDecimal("199.99"));
        updatedBook.setAuthor("updatedAuthor");
        updatedBook.setIsbn("1000000000001");
        updatedBook.setTitle("updateName");
        updatedBook.setCategoryIds(new HashSet<>());

        // When
        MvcResult result = mockMvc.perform(
                        put("/api/books/{id}", 4L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedBook))
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(actual.getTitle(), updatedBook.getTitle());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:databases/book/06-insert-for-delete-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:databases/book/07-delete-for-delete-test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void delete_Success() throws Exception {
        // Given
        long bookId = 6L;

        // When
        mockMvc.perform(delete("/api/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("databases/book/03-delete-books.sql")
            );
        }
    }
}
