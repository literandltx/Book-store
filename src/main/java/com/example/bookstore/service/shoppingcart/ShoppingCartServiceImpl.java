package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartResponseDto getByUserId(Long userId) {
        return shoppingCartMapper.toDto(getShoppingCart(userId));
    }

    // todo fix output delay (maybe the problem presents everywhere)
    @Override
    public ShoppingCartResponseDto addCartItem(Long userId, CreateCartItemRequestDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCart(userId);
        CartItem cartItem = cartItemMapper.toModel(requestDto);

        if (shoppingCart.getCartItems().stream()
                .anyMatch(item -> item.getBook().getId().equals(requestDto.getBookId()))) {
            throw new RuntimeException("This book already added in shopping cart");
        }

        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto updateCartItem(
            Long userId,
            Long cartItemId,
            UpdateCartItemRequestDto requestDto
    ) {
        if (getShoppingCart(userId).getCartItems()
                .stream()
                .noneMatch(item -> item.getId().equals(cartItemId))) {
            throw new RuntimeException("User cannot update other's cart item by: " + cartItemId);
        }

        CartItem cartItemFromDb = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item by id " + cartItemId));

        CartItem cartItem = cartItemMapper.toModel(requestDto);
        cartItemFromDb.setQuantity(cartItem.getQuantity());
        cartItemRepository.save(cartItemFromDb);

        return shoppingCartMapper.toDto(getShoppingCart(userId));
    }

    @Override
    public ShoppingCartResponseDto deleteCartItem(Long userId, Long cartItemId) {
        if (getShoppingCart(userId).getCartItems()
                .stream()
                .noneMatch(item -> item.getId().equals(cartItemId))) {
            throw new RuntimeException("User cannot delete other's cart item by: " + cartItemId);
        }

        cartItemRepository.deleteById(cartItemId);
        return shoppingCartMapper.toDto(getShoppingCart(userId));
    }

    private ShoppingCart getShoppingCart(Long userId) {
        return shoppingCartRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Cannot find shopping cart by id: " + userId));
    }
}
