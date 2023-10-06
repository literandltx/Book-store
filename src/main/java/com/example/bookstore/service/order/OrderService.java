package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.model.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    List<OrderResponseDto> getOrderHistory(User user, Pageable pageable);

    Set<OrderItemResponseDto> getOrderItemsByOrderId(User user, Long orderId);

    OrderItemResponseDto getOrderItemByIdByOrderId(User user, Long orderId, Long itemId);

    OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto);

    OrderResponseDto updateOrderStatusById(User user, Long id, UpdateOrderRequestDto requestDto);
}
