package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.example.bookstore.dto.shoppingcart.CartItemResponseDto;
import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.CartItemRepository;
import com.example.bookstore.repository.ShoppingCartRepository;
import com.example.bookstore.service.shoppingcart.ShoppingCartServiceImpl;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartServiceImpl;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    void getById_Success() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();
        Long userId = shoppingCart.getUser().getId();

        ShoppingCartResponseDto expected = getShoppingCartResponse(shoppingCart);

        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.getByUserId(userId);

        // Then
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    void addCartItem_Success() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();
        Long userId = shoppingCart.getUser().getId();

        CartItem duplicateCartItem = getDuplicateCartItem(shoppingCart);

        CreateCartItemRequestDto duplicateCreateCartItemRequestDto =
                getCreateCartItemRequestDtoByCartItem(duplicateCartItem);

        Mockito.when(cartItemMapper.toModel(duplicateCreateCartItemRequestDto)).thenReturn(
                duplicateCartItem);
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(
                Optional.of(shoppingCart));

        // When
        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> shoppingCartServiceImpl.addCartItem(
                                userId, duplicateCreateCartItemRequestDto));

        // Then
        String expected = "This book already added in shopping cart";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verify(cartItemMapper, times(1)).toModel(duplicateCreateCartItemRequestDto);
        verifyNoMoreInteractions(shoppingCartRepository, cartItemMapper);
    }

    @Test
    void updateCartItem_Success() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();
        final Long userId = shoppingCart.getUser().getId();

        final Long cartItemId = 1L;

        UpdateCartItemRequestDto updateCartItemRequestDto = new UpdateCartItemRequestDto();
        updateCartItemRequestDto.setQuantity(11);

        CartItem updateCartItem = new CartItem();
        updateCartItem.setQuantity(updateCartItemRequestDto.getQuantity());

        shoppingCart.getCartItems().forEach(
                s -> s.setQuantity(updateCartItemRequestDto.getQuantity()));

        ShoppingCartResponseDto expected = getShoppingCartResponse(shoppingCart);

        CartItem cartItemFromDb = shoppingCart.getCartItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't get test CartItem"));

        Mockito.when(cartItemRepository.findById(cartItemId))
                .thenReturn(Optional.of(cartItemFromDb));
        Mockito.when(cartItemMapper.toModel(updateCartItemRequestDto)).thenReturn(updateCartItem);
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        // When
        ShoppingCartResponseDto actual =
                shoppingCartServiceImpl.updateCartItem(userId, cartItemId,
                        updateCartItemRequestDto);
        // Then
        assertEquals(expected, actual);
        verify(cartItemRepository, times(1)).save(any());
        verify(cartItemMapper, times(1)).toModel(updateCartItemRequestDto);
        verify(shoppingCartRepository, times(2)).findById(userId);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper, cartItemMapper,
                cartItemRepository);
    }

    @Test
    void updateCartItem_NotSuccess() {
        // Given
        ShoppingCart shoppingCart = getShoppingCart();
        Long userId = shoppingCart.getUser().getId();

        Long cartItemId = 1L;

        // When
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartServiceImpl.updateCartItem(userId, cartItemId,
                                any()));
        // Then
        String expected = "Cannot find shopping cart by id: " + cartItemId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    void getShoppingCartById_Success() {
        // Given
        Long userId = -1L;
        Mockito.when(shoppingCartRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartServiceImpl.getByUserId(userId));

        // Then
        String expected = "Cannot find shopping cart by id: " + userId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    private ShoppingCartResponseDto getShoppingCartResponse(ShoppingCart shoppingCart) {
        Set<CartItemResponseDto> cartItemResponseDtoSet = shoppingCart.getCartItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toSet());

        ShoppingCartResponseDto shoppingCartResponseDto = new ShoppingCartResponseDto();
        shoppingCartResponseDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartResponseDto.setId(shoppingCart.getId());
        shoppingCartResponseDto.setCartItems(cartItemResponseDtoSet);

        return shoppingCartResponseDto;
    }

    private CartItemResponseDto mapToResponse(CartItem cartItem) {
        CartItemResponseDto responseDto = new CartItemResponseDto();
        responseDto.setId(cartItem.getId());
        responseDto.setBookId(cartItem.getBook().getId());
        responseDto.setBookTitle(cartItem.getBook().getTitle());
        responseDto.setQuantity(cartItem.getQuantity());
        return responseDto;
    }

    private ShoppingCart getShoppingCart() {
        User user = new User();
        user.setId(1L);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("testBook");

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(1);
        cartItem.setBook(book);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(user.getId());
        shoppingCart.setCartItems(cartItems);

        return shoppingCart;
    }

    private CreateCartItemRequestDto getCreateCartItemRequestDtoByCartItem(CartItem cartItem) {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(cartItem.getBook().getId());
        requestDto.setQuantity(cartItem.getQuantity());

        return requestDto;
    }

    private CartItem getNewCartItem(ShoppingCart shoppingCart) {
        Book book = new Book();
        book.setId(3L);
        book.setTitle("testBook1");

        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setQuantity(4);
        cartItem.setBook(book);
        return cartItem;
    }

    private CartItem getDuplicateCartItem(ShoppingCart shoppingCart) {
        CartItem cartItem = getNewCartItem(shoppingCart);
        cartItem.getBook().setId(1L);
        return cartItem;
    }
}
