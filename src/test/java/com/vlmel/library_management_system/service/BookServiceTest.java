package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks_returnsMappedResponses() {
        var book1 = bookEntity(1L, "Clean Code", "Robert Martin");
        var book2 = bookEntity(2L, "Effective Java", "Joshua Bloch");

        var response1 = bookResponse(1L, "Clean Code", "Robert Martin");
        var response2 = bookResponse(2L, "Effective Java", "Joshua Bloch");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookMapper.toBookResponse(book1)).thenReturn(response1);
        when(bookMapper.toBookResponse(book2)).thenReturn(response2);

        var result = bookService.getAllBooks();

        assertEquals(List.of(response1, response2), result);
        verify(bookRepository).findAll();
    }

    @Test
    void getAllBooks_returnsEmptyList_whenNoBooks() {
        when(bookRepository.findAll()).thenReturn(List.of());

        var result = bookService.getAllBooks();

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll();
    }

    private static BookEntity bookEntity(Long id, String title, String author) {
        var book = new BookEntity();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn("9780306406157");
        book.setPublishedYear(2008);
        return book;
    }

    private static GetBookResponse bookResponse(Long id, String title, String author) {
        var response = new GetBookResponse();
        response.setId(id);
        response.setTitle(title);
        response.setAuthor(author);
        response.setIsbn("9780306406157");
        response.setPublishedYear(2008);
        return response;
    }
}
