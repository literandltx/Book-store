package com.example.bookstore.dto.shoppingcart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Min(1)
    @NotNull
    private Long bookId;
    @Min(1)
    @NotNull
    private Integer quantity;
}
