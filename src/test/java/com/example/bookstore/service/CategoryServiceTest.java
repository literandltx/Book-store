package com.example.bookstore.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.category.CategoryRequestDto;
import com.example.bookstore.dto.category.CategoryResponseDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CategoryMapper;
import com.example.bookstore.model.Category;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.category.CategoryServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void findAll_WhenRepositoryEmpty_Success() {
        // Given
        Category category = getCategory();
        CategoryResponseDto expected = toResponseDto(category);

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        List<CategoryResponseDto> actual = categoryService.findAll(pageable);

        // Then
        assertThat(actual).hasSize(1);
    }

    @Test
    public void findAll_WhenRepositoryNotEmpty_Success() {
        // When
        when(categoryRepository.findAll(Mockito.<Pageable>any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        // Then
        assertTrue(categoryService.findAll(null).isEmpty());
        verify(categoryRepository).findAll(Mockito.<Pageable>any());
    }

    @Test
    public void getById_Exist() {
        // Given
        Long categoryId = 1L;
        Category category = getCategory();
        CategoryResponseDto expected = toResponseDto(category);

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryResponseDto actual = categoryService.getById(categoryId);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getById_NotExist() {
        // Given
        Long categoryId = -100L;
        Category category = getCategory();
        CategoryResponseDto expected = toResponseDto(category);

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(categoryId));
    }

    @Test
    public void updateById_Exist() {
        // Given
        Category category = getCategory();

        CategoryRequestDto requestDto = toRequestDto(category);
        CategoryResponseDto expected = toResponseDto(category);

        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);

        // When
        CategoryResponseDto actual = categoryService.updateById(1L, requestDto);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void updateById_NotExist() {
        // Given
        Category category = getCategory();
        CategoryRequestDto requestDto = toRequestDto(category);

        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        // Then
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateById(1L, requestDto));
    }

    @Test
    public void save_Success() {
        // Given
        Category category = getCategory();
        CategoryRequestDto requestDto = toRequestDto(category);
        CategoryResponseDto expected = toResponseDto(category);

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        // When
        CategoryResponseDto actual = categoryService.save(requestDto);

        // Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteById_Test() {
        // Given
        doNothing().when(categoryRepository).deleteById(anyLong());

        // When
        categoryService.deleteById(1L);

        // Then
        verify(categoryRepository).deleteById(anyLong());
    }

    private CategoryResponseDto toResponseDto(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setName(category.getName());
        categoryResponseDto.setDescription(category.getDescription());
        return categoryResponseDto;
    }

    private CategoryRequestDto toRequestDto(Category category) {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName(category.getName());
        requestDto.setDescription(category.getDescription());
        return requestDto;
    }

    private Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category A");
        category.setDescription("Description category A");

        return category;
    }
}
