package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
