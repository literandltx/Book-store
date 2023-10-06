package com.example.bookstore.dto.order;

import com.example.bookstore.model.Order;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderRequestDto {
    @NotBlank
    private Order.Status status;
}
