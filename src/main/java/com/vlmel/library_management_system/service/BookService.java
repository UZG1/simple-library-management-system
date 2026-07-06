package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.response.CreateBookResponse;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public List<GetBookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    public CreateBookResponse createBook(CreateBookRequest request) {
        BookEntity bookEntity =  bookRepository.save(
                bookMapper.toBookEntity(request)
        );
        return bookMapper.toCreateBookResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    public GetBookDetailsResponse getBookById(Long id) {
        GetBookDetailsResponse getBookDetailsResponse =  bookRepository.findById(id)
                .map(bookMapper::toBookDetailsResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return getBookDetailsResponse;
    }
}
