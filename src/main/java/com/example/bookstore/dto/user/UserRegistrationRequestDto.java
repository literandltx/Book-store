package com.example.bookstore.dto.user;

import com.example.bookstore.validation.Email;
import com.example.bookstore.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@FieldMatch(
        first = "password",
        second = "repeatPassword",
        message = "Password and repeat password shouldn't be empty and should be equal"
)
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
