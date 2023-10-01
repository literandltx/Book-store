package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface ShoppingCartService {
    ShoppingCartResponseDto getByUserId(Long userId);

    ShoppingCartResponseDto addCartItem(Long userId,
                                        CreateCartItemRequestDto requestDto);

    ShoppingCartResponseDto updateCartItem(Long userId,
                                           Long cartItemId,
                                           UpdateCartItemRequestDto requestDto);

    ShoppingCartResponseDto deleteCartItem(Long userId,
                                           Long cartItemId);
}
