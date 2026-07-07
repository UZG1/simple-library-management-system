package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;

import java.util.List;

public interface BookService {
    List<GetBookResponse> getAllBooks();

    GetBookWithoutCopiesResponse createBook(CreateBookRequest request);

    GetBookDetailsResponse getBookById(Long id);

    GetBookWithoutCopiesResponse updateBook(Long id, UpdateBookRequest request);

    void deleteBookById(Long id);

    List<GetCopyOfBookResponse> getAvailableCopiesForBook(Long id);

    GetCopyOfBookResponse createBookCopy(Long id);

    GetCopyOfBookResponse updateAvailabilityStatus(Long id, Long copyId, UpdateCopyBookStatusRequest request);
}
