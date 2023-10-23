package com.example.bookstore.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.shoppingcart.CartItemResponseDto;
import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
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
                    new ClassPathResource(
                            "databases/user/01-insert-user-and-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/book/01-insert-3-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/cart-item/01-insert-cart-item.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/cart-item/04-delete-all-cart-items.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/user/02-delete-user-and-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "databases/book/03-delete-books.sql")
            );
        }
    }

    @Test
    void getShoppingCart_GivenShoppingCartsCatalog_ReturnsUsersShoppingCart() throws Exception {
        // Given
        User mockUser = getMockUser();
        ShoppingCartResponseDto expected = getShoppingCartResponseDto();

        // When
        MvcResult result = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertEquals(expected, actual);
    }

    @Test
    @Sql(
            scripts = "classpath:databases/cart-item/03-delete-cart-item.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addBookToShoppingCart_GivenShoppingCartAndNewBook_ReturnsUpdatedShoppingCart()
            throws Exception {
        // Given
        User mockUser = getMockUser();

        CreateCartItemRequestDto expected = new CreateCartItemRequestDto();
        expected.setBookId(4L);
        expected.setQuantity(3);

        String jsonRequest = objectMapper.writeValueAsString(expected);

        // When
        MvcResult result = mockMvc.perform(post("/cart")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertNotNull(actual);
        assertThat(actual.getCartItems()).hasSize(2);
        assertTrue(actual.getCartItems().stream()
                .anyMatch(c -> expected.getBookId().equals((c.getBookId()))
                        && Objects.equals(expected.getQuantity(), c.getQuantity())));
    }

    @Test
    @Sql(
            scripts = "classpath:databases/cart-item/05-insert-for-delete.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:databases/cart-item/06-delete-for-delete.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteCartItem_GivenShoppingCartWithOneItem_ReturnsEmptyShoppingCart() throws Exception {
        // Given
        User mockUser = getMockUser();
        ShoppingCartResponseDto expected = getShoppingCartResponseDto();

        long cartItemId = 20L;

        // When
        MvcResult result = mockMvc.perform(delete("/cart/cart-items/" + cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();
        // get
        MvcResult resultGet = mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        ShoppingCartResponseDto actualGet = objectMapper.readValue(resultGet.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertEquals(expected, actualGet);

        ShoppingCartResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), ShoppingCartResponseDto.class);
        assertNotNull(actual);

        assertThat(actualGet.getCartItems()).hasSize(1);
    }

    private ShoppingCartResponseDto getShoppingCartResponseDto() {
        ShoppingCartResponseDto shoppingCartResponseDto = new ShoppingCartResponseDto();
        shoppingCartResponseDto.setId(1L);
        shoppingCartResponseDto.setUserId(1L);

        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setId(10L);
        cartItemResponseDto.setBookId(3L);
        cartItemResponseDto.setBookTitle("book1");
        cartItemResponseDto.setQuantity(1);

        Set<CartItemResponseDto> cartItems = new HashSet<>();
        cartItems.add(cartItemResponseDto);
        shoppingCartResponseDto.setCartItems(cartItems);
        return shoppingCartResponseDto;
    }

    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName(Role.RoleName.ROLE_USER);
        roles.add(role);
        user.setRoles(roles);
        return user;
    }
}
