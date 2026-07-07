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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GetBookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<GetBookWithoutCopiesResponse> createBook(@RequestBody @Valid CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBookDetailsResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetBookWithoutCopiesResponse> updateBookById(@PathVariable Long id,
                                                                       @RequestBody @Valid UpdateBookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/copies")
    public ResponseEntity<List<GetCopyOfBookResponse>> getAllAvailableCopiesOfBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getAvailableCopiesForBook(id));
    }

    @PostMapping("/{id}/copies")
    public ResponseEntity<GetCopyOfBookResponse> createCopyForBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBookCopy(id));
    }

    @PutMapping("/{id}/copies/{copyId}")
    public ResponseEntity<GetCopyOfBookResponse> updateCopyBookStatus(@PathVariable Long id,
                                                                      @PathVariable Long copyId,
                                                                      @RequestBody @Valid UpdateCopyBookStatusRequest request) {
        return ResponseEntity.ok(bookService.updateAvailabilityStatus(id, copyId, request));
    }
}
