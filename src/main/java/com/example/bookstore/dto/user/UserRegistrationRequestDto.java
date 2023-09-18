package com.example.bookstore.dto.user;

import com.example.bookstore.validation.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
