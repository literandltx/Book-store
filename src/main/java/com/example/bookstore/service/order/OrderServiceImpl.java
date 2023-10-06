package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderItemResponseDto;
import com.example.bookstore.dto.order.OrderResponseDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<OrderResponseDto> getOrderHistory(User user, Pageable pageable) {
        return orderRepository.findOrdersByUser(user, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public Set<OrderItemResponseDto> getOrderItemsByOrderId(User user, Long orderId) {
        Order order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(
                () -> new EntityNotFoundException("Cannot find order by id: " + orderId));

        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItemResponseDto getOrderItemByIdByOrderId(User user, Long orderId, Long itemId) {
        Order order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(
                () -> new EntityNotFoundException("Cannot find order by id: " + orderId));

        return orderItemMapper.toDto(
                orderItemRepository.findOrderItemByOrderIdAndId(order.getId(), itemId).orElseThrow(
                        () -> new EntityNotFoundException("Cannot find item by id:" + itemId)));
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(User user, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow();
        Order order = orderMapper.toEntity(requestDto);

        BigDecimal total = shoppingCart.getCartItems().stream()
                .map(bd -> bd.getBook().getPrice().multiply(BigDecimal.valueOf(bd.getQuantity())))
                .reduce(BigDecimal::add)
                .orElseThrow();

        Set<OrderItem> orderItems = getOrderItemFromShoppingCart(shoppingCart, order);

        order.setOrderDate(LocalDateTime.now());
        order.setUser(shoppingCart.getUser());
        order.setTotal(total);
        order.setOrderItems(orderItems);
        Order save = orderRepository.saveAndFlush(order);

        cartItemRepository.deleteByShoppingCartIAndId(shoppingCart.getUser().getId());

        return orderMapper.toDto(save);
    }

    @Override
    public OrderResponseDto updateOrderStatusById(
            User user,
            Long orderId,
            UpdateOrderRequestDto requestDto
    ) {
        Order order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(
                () -> new EntityNotFoundException("Cannot find order by id: " + orderId));

        order.setStatus(requestDto.getStatus());
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    private static Set<OrderItem> getOrderItemFromShoppingCart(
            ShoppingCart shoppingCart,
            Order order
    ) {
        return shoppingCart.getCartItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();

                    orderItem.setOrder(order);
                    orderItem.setBook(item.getBook());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getBook().getPrice());

                    return orderItem;
                })
                .collect(Collectors.toSet());
    }
}
