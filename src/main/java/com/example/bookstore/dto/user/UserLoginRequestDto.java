package com.example.bookstore.dto.user;

import com.example.bookstore.validation.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @NotNull
    @Size(min = 6, max = 100)
    private String password;
}
