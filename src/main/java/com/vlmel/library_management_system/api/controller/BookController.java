package com.vlmel.library_management_system.api.controller;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<GetBookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    public GetBookWithoutCopiesResponse createBook(@RequestBody @Valid CreateBookRequest request) {
        return bookService.createBook(request);
    }

    @GetMapping("/{id}")
    public GetBookDetailsResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/{id}")
    public GetBookWithoutCopiesResponse updateBookById(@PathVariable Long id,
                                                       @RequestBody @Valid UpdateBookRequest request) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable Long id) {
        //return no content 204
        bookService.deleteBookById(id);
    }

    @GetMapping("/{id}/copies")
    public List<GetCopyOfBookResponse> getAllAvailableCopiesOfBook(@PathVariable Long id) {
        return bookService.getAvailableCopiesForBook(id);
    }

    @PostMapping("/{id}/copies")
    public GetCopyOfBookResponse createCopyForBook(@PathVariable Long id) {
        return bookService.createBookCopy(id);
    }

    @PutMapping("/{id}/copies/{copyId}")
    public GetCopyOfBookResponse updateCopyBookStatus(@PathVariable Long id,
                                                      @PathVariable Long copyId,
                                                      @RequestBody @Valid UpdateCopyBookStatusRequest request) {
        return bookService.updateAvailabilityStatus(id, copyId, request);
    }
}
