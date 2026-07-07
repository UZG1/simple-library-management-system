package com.vlmel.library_management_system.api.controller;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
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
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Operations with books and their copies")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Returns a paginated list of books")
    @ApiResponse(responseCode = "200", description = "Books page successfully returned")
    public ResponseEntity<Page<GetBookResponse>> getAllBooks(
            @ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a book", description = "Creates a new book entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<GetBookWithoutCopiesResponse> createBook(@RequestBody @Valid CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book details", description = "Returns a book with its copies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<GetBookDetailsResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Updates book data by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<GetBookWithoutCopiesResponse> updateBookById(@PathVariable Long id,
                                                                       @RequestBody @Valid UpdateBookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Deletes a book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/copies")
    @Operation(summary = "Get available copies", description = "Returns all available copies for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available copies returned"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<List<GetCopyOfBookResponse>> getAllAvailableCopiesOfBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getAvailableCopiesForBook(id));
    }

    @PostMapping("/{id}/copies")
    @Operation(summary = "Create a copy", description = "Creates a new copy for a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Copy successfully created"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<GetCopyOfBookResponse> createCopyForBook(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBookCopy(id));
    }

    @PutMapping("/{id}/copies/{copyId}")
    @Operation(summary = "Update copy status", description = "Updates availability status for a specific copy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Copy status successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Book or copy not found")
    })
    public ResponseEntity<GetCopyOfBookResponse> updateCopyBookStatus(@PathVariable Long id,
                                                                      @PathVariable Long copyId,
                                                                      @RequestBody @Valid UpdateCopyBookStatusRequest request) {
        return ResponseEntity.ok(bookService.updateAvailabilityStatus(id, copyId, request));
    }
}
