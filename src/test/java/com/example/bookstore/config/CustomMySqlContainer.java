package com.example.bookstore.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String IMAGE_VERSION = "mysql:8";
    private static CustomMySqlContainer mySQLContainer;

    public CustomMySqlContainer() {
        super(IMAGE_VERSION);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mySQLContainer == null) {
            mySQLContainer = new CustomMySqlContainer();
        }
        return mySQLContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", mySQLContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", mySQLContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", mySQLContainer.getPassword());
    }

    @Override
    public void stop() {

    }
}
