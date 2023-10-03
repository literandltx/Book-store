package com.example.bookstore.mapper;

import com.example.bookstore.config.MapperConfig;
import com.example.bookstore.dto.shoppingcart.CartItemResponseDto;
import com.example.bookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.UpdateCartItemRequestDto;
import com.example.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class, uses = BookMapper.class)
public interface CartItemMapper {
    @Mappings({
            @Mapping(source = "book.id", target = "bookId"),
            @Mapping(source = "book.title", target = "bookTitle")
    })
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(target = "book", source = "bookId", qualifiedByName = "bookFromId")
    CartItem toModel(CreateCartItemRequestDto requestDto);

    @Mapping(target = "book", ignore = true)
    CartItem toModel(UpdateCartItemRequestDto requestDto);
}
