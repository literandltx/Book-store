# Book store

## Description
This project is an example of a Java application that implements a book catalog
and includes a testing module to verify its functionality. It utilizes Docker for
containerization and Swagger for API documentation. The "Online Bookstore" application
is a web application for the convenience of selecting and purchasing books online.
In the application, you can add, store, select, buy saved books from different categories,
which allows you to flexibly make purchases without the user directly visiting the store.
The application implements such Java-based server technologies as Spring Framework, JWT, Docker.

### Functionality
1. Book can be added, updated, deleted, shown 
2. Category can be added, updated, deleted, shown
3. To shopping cart can be added, updated, deleted and shown cart item
4. Order can be created using items from shopping cart
5. User authentication.

For more details use Swagger API


### Technologies
- Java 17;
- Maven, Docker;
- Spring Boot, Spring Security, JWT, Spring JPA;
- MySQL, Liquibase;
- JUnit 5, Mockito, Testcontainers;
- Lombok, Mapstruct, Jackson;
- REST, Swagger;


## Quickstart

### IDE
1. Clone the repository to your computer.
2. Open in favorite IDE.
3. Configure database connection if necessary.
4. Use maven to build and run application.

### Docker
1. Install docker.
2. Clone the repository to your computer.
3. In .env file configure database connection if necessary.
4. Open terminal and run
````
$ docker-compose up
````


# [Swagger API](http://localhost:8080/swagger-ui.html)