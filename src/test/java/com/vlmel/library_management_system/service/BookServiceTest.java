package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.exception.BookCopyNotFoundException;
import com.vlmel.library_management_system.exception.BookIsbnAlreadyExistsException;
import com.vlmel.library_management_system.exception.BookNotFoundException;
import com.vlmel.library_management_system.exception.BookTitleAlreadyExistsException;
import com.vlmel.library_management_system.mapper.BookCopyMapper;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import com.vlmel.library_management_system.repository.CopyBookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CopyBookRepository copyBookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookCopyMapper bookCopyMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void getAllBooks_returnsMappedResponses() {
        var pageable = PageRequest.of(0, 10);
        var book1 = bookEntity(1L, "Clean Code", "Robert Martin");
        var book2 = bookEntity(2L, "Effective Java", "Joshua Bloch");

        var response1 = bookResponse(1L, "Clean Code", "Robert Martin");
        var response2 = bookResponse(2L, "Effective Java", "Joshua Bloch");

        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(book1, book2), pageable, 2));
        when(bookMapper.toBookResponse(book1)).thenReturn(response1);
        when(bookMapper.toBookResponse(book2)).thenReturn(response2);

        var result = bookService.getAllBooks(pageable);

        assertEquals(List.of(response1, response2), result.getContent());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void getAllBooks_returnsEmptyList_whenNoBooks() {
        var pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        var result = bookService.getAllBooks(pageable);

        assertTrue(result.isEmpty());
        verify(bookRepository).findAll(pageable);
    }

    @Test
    void createBook_returnsCreatedBook() {
        var request = createBookRequest("Clean Code", "Robert Martin", "9780306406157", 2008);
        var entity = bookEntity(1L, "Clean Code", "Robert Martin");
        var response = bookWithoutCopiesResponse(1L, "Clean Code", "Robert Martin");

        when(bookRepository.existsByTitle("Clean Code")).thenReturn(false);
        when(bookRepository.existsByIsbn("9780306406157")).thenReturn(false);
        when(bookMapper.toBookEntity(request)).thenReturn(entity);
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toCreateBookResponse(entity)).thenReturn(response);

        var result = bookService.createBook(request);

        assertEquals(response, result);
        verify(bookRepository).save(entity);
    }

    @Test
    void createBook_throwsWhenTitleExists() {
        var request = createBookRequest("Clean Code", "Robert Martin", "9780306406157", 2008);
        when(bookRepository.existsByTitle("Clean Code")).thenReturn(true);

        assertThrows(BookTitleAlreadyExistsException.class, () -> bookService.createBook(request));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void createBook_throwsWhenIsbnExists() {
        var request = createBookRequest("Clean Code", "Robert Martin", "9780306406157", 2008);
        when(bookRepository.existsByTitle("Clean Code")).thenReturn(false);
        when(bookRepository.existsByIsbn("9780306406157")).thenReturn(true);

        assertThrows(BookIsbnAlreadyExistsException.class, () -> bookService.createBook(request));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookById_returnsBookWithCopies() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        var response = new GetBookDetailsResponse();
        response.setId(1L);
        response.setTitle("Clean Code");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDetailsResponse(book)).thenReturn(response);

        var result = bookService.getBookById(1L);

        assertEquals(response, result);
    }

    @Test
    void getBookById_throwsWhenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(99L));
    }

    @Test
    void updateBook_updatesOnlyProvidedFields() {
        var request = new UpdateBookRequest();
        request.setTitle("Clean Code (2nd Edition)");
        request.setPublishedYear(2010);

        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        var response = bookWithoutCopiesResponse(1L, "Clean Code (2nd Edition)", "Robert Martin");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitleAndIdNot("Clean Code (2nd Edition)", 1L)).thenReturn(false);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toCreateBookResponse(book)).thenReturn(response);

        var result = bookService.updateBook(1L, request);

        assertEquals(response, result);
        verify(bookMapper).updateBookFromRequest(request, book);
        verify(bookRepository).save(book);
    }

    @Test
    void updateBook_throwsWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.updateBook(99L, new UpdateBookRequest()));
    }

    @Test
    void updateBook_throwsWhenTitleExistsForAnotherBook() {
        var request = new UpdateBookRequest();
        request.setTitle("Effective Java");

        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitleAndIdNot("Effective Java", 1L)).thenReturn(true);

        assertThrows(BookTitleAlreadyExistsException.class, () -> bookService.updateBook(1L, request));
    }

    @Test
    void updateBook_throwsWhenIsbnExistsForAnotherBook() {
        var request = new UpdateBookRequest();
        request.setIsbn("9780134685991");

        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIdNot("9780134685991", 1L)).thenReturn(true);

        assertThrows(BookIsbnAlreadyExistsException.class, () -> bookService.updateBook(1L, request));
    }

    @Test
    void deleteBookById_deletesExistingBook() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBookById(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void deleteBookById_throwsWhenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBookById(99L));
        verify(bookRepository, never()).delete(any());
    }

    @Test
    void getAvailableCopiesForBook_returnsAvailableCopies() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        var copyEntity = copyEntity(1L, book, true);
        var copyResponse = copyResponse(1L, true);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(copyBookRepository.findBookCopyEntitiesByBookIdAndAvailable(1L, true))
                .thenReturn(List.of(copyEntity));
        when(bookMapper.toCopyOfBookResponse(List.of(copyEntity))).thenReturn(List.of(copyResponse));

        var result = bookService.getAvailableCopiesForBook(1L);

        assertEquals(List.of(copyResponse), result);
    }

    @Test
    void getAvailableCopiesForBook_throwsWhenBookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.getAvailableCopiesForBook(99L));
    }

    @Test
    void createBookCopy_createsCopyWithAvailableTrue() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        var savedCopy = copyEntity(10L, book, true);
        var response = copyResponse(10L, true);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(copyBookRepository.save(any(BookCopyEntity.class))).thenReturn(savedCopy);
        when(bookCopyMapper.toCopyResponse(savedCopy)).thenReturn(response);

        var result = bookService.createBookCopy(1L);

        assertEquals(response, result);

        var captor = ArgumentCaptor.forClass(BookCopyEntity.class);
        verify(copyBookRepository).save(captor.capture());
        assertEquals(book, captor.getValue().getBook());
        assertTrue(captor.getValue().getAvailable());
    }

    @Test
    void createBookCopy_throwsWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.createBookCopy(99L));
    }

    @Test
    void updateAvailabilityStatus_updatesCopy() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin");
        var copy = copyEntity(5L, book, true);
        var request = new UpdateCopyBookStatusRequest();
        request.setAvailable(false);
        var response = copyResponse(5L, false);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(copyBookRepository.findBookCopyEntityByIdAndBookId(5L, 1L)).thenReturn(Optional.of(copy));
        when(copyBookRepository.save(copy)).thenReturn(copy);
        when(bookCopyMapper.toCopyResponse(copy)).thenReturn(response);

        var result = bookService.updateAvailabilityStatus(1L, 5L, request);

        assertEquals(response, result);
        assertEquals(false, copy.getAvailable());
    }

    @Test
    void updateAvailabilityStatus_throwsWhenBookNotFound() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThrows(BookNotFoundException.class,
                () -> bookService.updateAvailabilityStatus(99L, 5L, new UpdateCopyBookStatusRequest()));
    }

    @Test
    void updateAvailabilityStatus_throwsWhenCopyNotFound() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        when(copyBookRepository.findBookCopyEntityByIdAndBookId(5L, 1L)).thenReturn(Optional.empty());

        assertThrows(BookCopyNotFoundException.class,
                () -> bookService.updateAvailabilityStatus(1L, 5L, new UpdateCopyBookStatusRequest()));
    }

    private static CreateBookRequest createBookRequest(String title, String author, String isbn, int year) {
        var request = new CreateBookRequest();
        request.setTitle(title);
        request.setAuthor(author);
        request.setIsbn(isbn);
        request.setPublishedYear(year);
        return request;
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

    private static BookCopyEntity copyEntity(Long id, BookEntity book, boolean available) {
        var copy = new BookCopyEntity();
        copy.setId(id);
        copy.setBook(book);
        copy.setAvailable(available);
        return copy;
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

    private static GetBookWithoutCopiesResponse bookWithoutCopiesResponse(Long id, String title, String author) {
        var response = new GetBookWithoutCopiesResponse();
        response.setId(id);
        response.setTitle(title);
        response.setAuthor(author);
        response.setIsbn("9780306406157");
        response.setPublishedYear(2008);
        return response;
    }

    private static GetCopyOfBookResponse copyResponse(Long id, boolean available) {
        var response = new GetCopyOfBookResponse();
        response.setId(id);
        response.setAvailable(available);
        return response;
    }
}
