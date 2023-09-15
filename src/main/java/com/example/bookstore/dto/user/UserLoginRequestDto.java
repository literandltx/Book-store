package com.example.bookstore.dto.user;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    // TODO
    //@Email
    private String email;
    @NotNull
    @Size(min = 6, max = 100)
    private String password;
}
