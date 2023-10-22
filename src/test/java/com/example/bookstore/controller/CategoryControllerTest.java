package com.example.bookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
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
class CategoryControllerTest {
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
                    new ClassPathResource("databases/category/01-insert-3-categories.sql")
            );

        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:databases/category/02-delete-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void createCategory_Success() throws Exception {
        // Given
        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(5L);
        expected.setName("name");
        expected.setDescription("description");

        String jsonRequest = objectMapper.writeValueAsString(expected);

        // When
        MvcResult result = mockMvc.perform(post("/category")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    void getCategoryById_Success() throws Exception {
        // Given
        long categoryId = 2L;
        CategoryResponseDto expected = new CategoryResponseDto();
        expected.setId(2L);
        expected.setName("Fantasy");

        // When
        MvcResult result = mockMvc.perform(get("/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CategoryResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        CategoryResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user")
    void getAll_Success() throws Exception {
        // Given
        List<CategoryResponseDto> expected = new ArrayList<>();
        CategoryResponseDto responseDto2 = new CategoryResponseDto();
        CategoryResponseDto responseDto3 = new CategoryResponseDto();

        expected.add(responseDto2);
        expected.add(responseDto3);

        responseDto2.setId(2L);
        responseDto2.setName("Fantasy");
        responseDto3.setId(3L);
        responseDto3.setName("Detective");

        MvcResult result = mockMvc.perform(
                        get("/category")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto[].class);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @Sql(
            scripts = "classpath:databases/category/03-insert-for-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCategory_Success() throws Exception {
        // Given
        CategoryRequestDto updateCategory = new CategoryRequestDto();
        updateCategory.setName("updatedName");

        // When
        MvcResult result = mockMvc.perform(
                        put("/category/{id}", 4)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateCategory))
                )
                .andExpect(status().isOk())
                .andReturn();
        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class);

        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(actual.getName(), updateCategory.getName());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCategory_Success() throws Exception {
        // Given
        long categoryId = 1L;

        // When
        mockMvc.perform(delete("/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("databases/category/02-delete-categories.sql")
            );
        }
    }
}
