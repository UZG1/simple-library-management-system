package com.vlmel.library_management_system.api.controller;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetAvailableCopyOfBook;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
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
    public GetBookResponse createBook(@RequestBody CreateBookRequest request) {
        return null;
    }

    @GetMapping
    public GetBookDetailsResponse getBookById() {
        return null;
    }

    @PutMapping("/{id}")
    public GetBookResponse updateBookById(@PathVariable String id, @RequestBody UpdateBookRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteBookById(@PathVariable String id) {
        //return no content 204
        return;
    }

    @GetMapping("/{id}/copies")
    public List<GetAvailableCopyOfBook> getAllAvailableCopiesOfBook(@PathVariable String id) {
        return null;
    }

    @PostMapping("/{id}/copies")
    public GetAvailableCopyOfBook createCopyForBook(@PathVariable String id) {
        return null;
    }

    @PutMapping("/{id}/copies/{copyId}")
    public GetAvailableCopyOfBook updateCopyBookStatus(@PathVariable String id,
                                                       @PathVariable String copyId,
                                                       UpdateCopyBookStatusRequest request) {

        return null;
    }

}
