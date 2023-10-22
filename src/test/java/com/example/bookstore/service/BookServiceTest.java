package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.service.book.BookServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    public void save_Success() {
        // Given
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book model = new Book();
        Book savedBook = new Book();

        when(bookMapper.toModel(requestDto)).thenReturn(model);
        when(bookRepository.save(model)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(new BookDto());

        // When
        BookDto result = bookService.save(requestDto);

        // Then
        assertNotNull(result);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(model);
        verify(bookMapper, times(1)).toDto(savedBook);
    }

    @Test
    public void findAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(new Book(), new Book());
        Page<Book> pageBooks = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(pageBooks);

        List<BookDto> expectedDtos = new ArrayList<>();
        expectedDtos.add(new BookDto());
        expectedDtos.add(new BookDto());
        when(bookMapper.toDto(any(Book.class)))
                .thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        // When
        List<BookDto> result = bookService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos.size(), result.size());
        for (int i = 0; i < expectedDtos.size(); i++) {
            assertEquals(expectedDtos.get(i), result.get(i));
        }
    }

    @Test
    public void findById_Success() {
        // Given
        Long bookId = 1L;
        Book book = new Book();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        BookDto expectedDto = new BookDto();
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        // When
        BookDto result = bookService.findById(bookId);

        // Then
        assertEquals(expectedDto, result);
    }

    @Test
    public void findById_EntityNotFoundException() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(bookId));
    }

    @Test
    public void updateById_Success() {
        // Given
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        // When
        BookDto result = bookService.updateById(bookId, requestDto);

        // Then
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
    }

    @Test
    public void updateById_EntityNotFoundException() {
        // Given
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // When and Then
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(bookId, requestDto));
    }

    @Test
    public void deleteById_Success() {
        // Given
        Long bookId = 1L;

        // When
        bookService.deleteById(bookId);

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    public void findAllByCategoryId_Success() {
        // Given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        books.add(new Book());

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(books);

        List<BookDtoWithoutCategoryIds> expectedDtos = new ArrayList<>();
        expectedDtos.add(new BookDtoWithoutCategoryIds());
        expectedDtos.add(new BookDtoWithoutCategoryIds());
        when(bookMapper.toDtoWithoutCategories(any(Book.class)))
                .thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        // When
        List<BookDtoWithoutCategoryIds> result = bookService
                .findAllByCategoryId(categoryId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos.size(), result.size());
        for (int i = 0; i < expectedDtos.size(); i++) {
            assertEquals(expectedDtos.get(i), result.get(i));
        }
    }
}
