package com.vlmel.library_management_system.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.vlmel.library_management_system.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    private static final String BOOKS_URL = "/books";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks_returnsOk() throws Exception {
        var response = new GetBookResponse();
        response.setId(1L);
        response.setTitle("Clean Code");
        response.setAuthor("Robert Martin");
        response.setIsbn("9780306406157");
        response.setPublishedYear(2008);

        var pageable = PageRequest.of(0, 20);
        when(bookService.getAllBooks(any())).thenReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get(BOOKS_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));
    }

    @Test
    void getAllBooks_returnsBadRequest_whenSortPropertyInvalid() throws Exception {
        var propertyException = mock(PropertyReferenceException.class);
        when(propertyException.getMessage()).thenReturn("No property 'unknown' found for type 'GetBookResponse'");
        when(bookService.getAllBooks(any())).thenThrow(propertyException);

        mockMvc.perform(get(BOOKS_URL).param("sort", "unknown,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("No property 'unknown' found for type 'GetBookResponse'"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL));
    }

    @Test
    void createBook_returnsCreated() throws Exception {
        var response = bookWithoutCopiesResponse(1L, "Clean Code", "Robert Martin");
        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(response);

        mockMvc.perform(post(BOOKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateBookRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void createBook_returnsBadRequest_whenValidationFails() throws Exception {
        var request = validCreateBookRequest();
        request.setTitle("");

        mockMvc.perform(post(BOOKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL))
                .andExpect(jsonPath("$.details[0]").value("title: Title is required"));
    }

    @Test
    void createBook_returnsBadRequest_whenJsonMalformed() throws Exception {
        mockMvc.perform(post(BOOKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Malformed JSON request"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL));
    }

    @Test
    void createBook_returnsConflict_whenTitleExists() throws Exception {
        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(new BookTitleAlreadyExistsException("Clean Code"));

        mockMvc.perform(post(BOOKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateBookRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Book with title 'Clean Code' already exists"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL));
    }

    @Test
    void createBook_returnsConflict_whenIsbnExists() throws Exception {
        when(bookService.createBook(any(CreateBookRequest.class)))
                .thenThrow(new BookIsbnAlreadyExistsException("9780306406157"));

        mockMvc.perform(post(BOOKS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCreateBookRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Book with ISBN '9780306406157' already exists"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL));
    }

    @Test
    void getBookById_returnsOk() throws Exception {
        var response = new GetBookDetailsResponse();
        response.setId(1L);
        response.setTitle("Clean Code");
        when(bookService.getBookById(1L)).thenReturn(response);

        mockMvc.perform(get(BOOKS_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void getBookById_returnsNotFound_whenBookMissing() throws Exception {
        when(bookService.getBookById(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get(BOOKS_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99"));
    }

    @Test
    void getBookById_returnsBadRequest_whenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get(BOOKS_URL + "/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(
                        "Invalid value 'abc' for parameter 'id'. Expected type: Long"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/abc"));
    }

    @Test
    void updateBook_returnsOk() throws Exception {
        var request = new UpdateBookRequest();
        request.setTitle("Clean Code (2nd Edition)");
        var response = bookWithoutCopiesResponse(1L, "Clean Code (2nd Edition)", "Robert Martin");

        when(bookService.updateBook(eq(1L), any(UpdateBookRequest.class))).thenReturn(response);

        mockMvc.perform(put(BOOKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code (2nd Edition)"));
    }

    @Test
    void updateBook_returnsNotFound_whenBookMissing() throws Exception {
        when(bookService.updateBook(eq(99L), any(UpdateBookRequest.class)))
                .thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(put(BOOKS_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99"));
    }

    @Test
    void updateBook_returnsConflict_whenTitleExists() throws Exception {
        var request = new UpdateBookRequest();
        request.setTitle("Effective Java");

        when(bookService.updateBook(eq(1L), any(UpdateBookRequest.class)))
                .thenThrow(new BookTitleAlreadyExistsException("Effective Java"));

        mockMvc.perform(put(BOOKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Book with title 'Effective Java' already exists"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1"));
    }

    @Test
    void updateBook_returnsConflict_whenIsbnExists() throws Exception {
        var request = new UpdateBookRequest();
        request.setIsbn("9780134685991");

        when(bookService.updateBook(eq(1L), any(UpdateBookRequest.class)))
                .thenThrow(new BookIsbnAlreadyExistsException("9780134685991"));

        mockMvc.perform(put(BOOKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Book with ISBN '9780134685991' already exists"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1"));
    }

    @Test
    void updateBook_returnsBadRequest_whenIsbnInvalid() throws Exception {
        var request = new UpdateBookRequest();
        request.setIsbn("invalid");

        mockMvc.perform(put(BOOKS_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1"));
    }

    @Test
    void deleteBook_returnsNoContent() throws Exception {
        mockMvc.perform(delete(BOOKS_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_returnsNotFound_whenBookMissing() throws Exception {
        doThrow(new BookNotFoundException(99L)).when(bookService).deleteBookById(99L);

        mockMvc.perform(delete(BOOKS_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99"));
    }

    @Test
    void getAvailableCopies_returnsOk() throws Exception {
        var copy = new GetCopyOfBookResponse();
        copy.setId(5L);
        copy.setAvailable(true);
        when(bookService.getAvailableCopiesForBook(1L)).thenReturn(List.of(copy));

        mockMvc.perform(get(BOOKS_URL + "/1/copies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void getAvailableCopies_returnsNotFound_whenBookMissing() throws Exception {
        when(bookService.getAvailableCopiesForBook(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(get(BOOKS_URL + "/99/copies"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99/copies"));
    }

    @Test
    void createCopy_returnsCreated() throws Exception {
        var copy = new GetCopyOfBookResponse();
        copy.setId(10L);
        copy.setAvailable(true);
        when(bookService.createBookCopy(1L)).thenReturn(copy);

        mockMvc.perform(post(BOOKS_URL + "/1/copies"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createCopy_returnsNotFound_whenBookMissing() throws Exception {
        when(bookService.createBookCopy(99L)).thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(post(BOOKS_URL + "/99/copies"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99/copies"));
    }

    @Test
    void updateCopyStatus_returnsOk() throws Exception {
        var request = new UpdateCopyBookStatusRequest();
        request.setAvailable(false);
        var copy = new GetCopyOfBookResponse();
        copy.setId(5L);
        copy.setAvailable(false);

        when(bookService.updateAvailabilityStatus(eq(1L), eq(5L), any(UpdateCopyBookStatusRequest.class)))
                .thenReturn(copy);

        mockMvc.perform(put(BOOKS_URL + "/1/copies/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateCopyStatus_returnsNotFound_whenBookMissing() throws Exception {
        var request = new UpdateCopyBookStatusRequest();
        request.setAvailable(true);

        when(bookService.updateAvailabilityStatus(eq(99L), eq(5L), any(UpdateCopyBookStatusRequest.class)))
                .thenThrow(new BookNotFoundException(99L));

        mockMvc.perform(put(BOOKS_URL + "/99/copies/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Book not found with id: 99"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/99/copies/5"));
    }

    @Test
    void updateCopyStatus_returnsNotFound_whenCopyMissing() throws Exception {
        var request = new UpdateCopyBookStatusRequest();
        request.setAvailable(true);

        when(bookService.updateAvailabilityStatus(eq(1L), eq(5L), any(UpdateCopyBookStatusRequest.class)))
                .thenThrow(new BookCopyNotFoundException(5L, 1L));

        mockMvc.perform(put(BOOKS_URL + "/1/copies/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Copy not found with id: 5 for book id: 1"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1/copies/5"));
    }

    @Test
    void updateCopyStatus_returnsBadRequest_whenAvailableMissing() throws Exception {
        mockMvc.perform(put(BOOKS_URL + "/1/copies/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").value("available: Available status is required"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1/copies/5"));
    }

    @Test
    void updateCopyStatus_returnsBadRequest_whenCopyIdIsNotNumeric() throws Exception {
        var request = new UpdateCopyBookStatusRequest();
        request.setAvailable(true);

        mockMvc.perform(put(BOOKS_URL + "/1/copies/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Invalid value 'abc' for parameter 'copyId'. Expected type: Long"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1/copies/abc"));
    }

    @Test
    void getBookById_returnsInternalServerError_whenUnexpectedException() throws Exception {
        when(bookService.getBookById(1L)).thenThrow(new RuntimeException("db failure"));

        mockMvc.perform(get(BOOKS_URL + "/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Unexpected server error"))
                .andExpect(jsonPath("$.details[0]").value("RuntimeException"))
                .andExpect(jsonPath("$.path").value(BOOKS_URL + "/1"));
    }

    private static CreateBookRequest validCreateBookRequest() {
        var request = new CreateBookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Robert Martin");
        request.setIsbn("9780306406157");
        request.setPublishedYear(2008);
        return request;
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
}
