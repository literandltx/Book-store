package com.example.bookstore.controller;

import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.example.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart manager",
        description = "CRUD operation over shoppingCart and  cartItem")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add cart item to user's cart",
            description = "Create and add cart item to user's cart in db")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping
    public ShoppingCartResponseDto createCartItem(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addCartItem(user.getId(), requestDto);
    }

    @Operation(summary = "Get all shopping cart's items",
            description = "Get all shopping cart's items that have user who send request")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping
    public ShoppingCartResponseDto getAllCartItems(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getByUserId(user.getId());
    }

    @Operation(summary = "Update cart item by id from user's shopping cart",
            description = "Update cart item by id, user can update only own cart item")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PutMapping("/cart-items/{cartItemId}")
    public ShoppingCartResponseDto updateCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequestDto requestDto
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateCartItem(user.getId(), cartItemId, requestDto);
    }

    @Operation(summary = "Delete cart item by id from user's shopping cart",
            description = "Delete cart item by id, user can update only own cart item")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @DeleteMapping("/cart-items/{cartItemId}")
    public ShoppingCartResponseDto deleteCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.deleteCartItem(user.getId(), cartItemId);
    }
}
