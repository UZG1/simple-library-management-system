package com.vlmel.library_management_system.api.controller;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetAvailableCopyOfBook;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import jakarta.validation.Valid;
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
public class BookController {

    @GetMapping
    public List<GetBookResponse> getAllBooks() {
        return null;
    }

    @PostMapping
    public GetBookResponse createBook(@RequestBody @Valid CreateBookRequest request) {
        return null;
    }

    @GetMapping("/{id}")
    public GetBookDetailsResponse getBookById(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}")
    public GetBookResponse updateBookById(@PathVariable Long id, @RequestBody @Valid UpdateBookRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable Long id) {
        //return no content 204
    }

    @GetMapping("/{id}/copies")
    public List<GetAvailableCopyOfBook> getAllAvailableCopiesOfBook(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/{id}/copies")
    public GetAvailableCopyOfBook createCopyForBook(@PathVariable Long id) {
        return null;
    }

    @PutMapping("/{id}/copies/{copyId}")
    public GetAvailableCopyOfBook updateCopyBookStatus(@PathVariable Long id,
                                                       @PathVariable Long copyId,
                                                       @RequestBody @Valid UpdateCopyBookStatusRequest request) {
        return null;
    }
}
