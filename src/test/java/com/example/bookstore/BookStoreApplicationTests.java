package com.example.bookstore;

import com.example.bookstore.config.CustomMySqlContainer;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = BookStoreApplicationTests.class)
class BookStoreApplicationTests {

    @ClassRule
    public static CustomMySqlContainer mySQLContainer = CustomMySqlContainer.getInstance();

    @Test
    void contextLoads() {
    }

}
