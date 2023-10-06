package com.example.bookstore.controller;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order manager",
        description = "Order manager can create, get orders, order an item from order")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get all orders.",
            description = "Get all orders currently have user.")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping
    public List<OrderResponseDto> getOrderHistory(
            Authentication authentication,
            Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();

        return orderService.getOrderHistory(user, pageable);
    }

    @Operation(summary = "Get order by id",
            description = "Get specific order by id, if user have one.")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/{orderId}/items")
    public Set<OrderItemResponseDto> getOrderHistoryById(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        User user = (User) authentication.getPrincipal();

        return orderService.getOrderItemsByOrderId(user, orderId);
    }

    @Operation(summary = "Get item by itemId from order by id",
            description = "Get item from order if user have that order and item into order")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemResponseDto getOrderItemByIdByOrderId(
            Authentication authentication,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        User user = (User) authentication.getPrincipal();

        return orderService.getOrderItemByIdByOrderId(user, orderId, itemId);
    }

    @Operation(summary = "Create order",
            description = "Create order using cart items, delete all items from cart if success")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping
    public OrderResponseDto createOrder(
            Authentication authentication,
            @RequestBody @Valid CreateOrderRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();

        return orderService.createOrder(user, requestDto);
    }

    @Operation(summary = "Update order status",
            description = "Update status by id, current options: DELIVERED or PENDING")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatusById(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();

        return orderService.updateOrderStatusById(user, id, requestDto);
    }
}
