package com.vlmel.library_management_system.service;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.mapper.BookCopyMapper;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import com.vlmel.library_management_system.repository.CopyBookRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CopyBookRepository copyBookRepository;
    private final BookMapper bookMapper;
    private final BookCopyMapper bookCopyMapper;

    @Override
    public List<GetBookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    @Override
    public GetBookWithoutCopiesResponse createBook(CreateBookRequest request) {
        BookEntity bookEntity = bookRepository.save(
                bookMapper.toBookEntity(request)
        );
        return bookMapper.toCreateBookResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public GetBookDetailsResponse getBookById(Long id) {
        //   TODO     поменять на кастомные ексепшины
        GetBookDetailsResponse getBookDetailsResponse = bookRepository.findById(id)
                .map(bookMapper::toBookDetailsResponse)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return getBookDetailsResponse;
    }

    @Transactional
    @Override
    public GetBookWithoutCopiesResponse updateBook(Long id, UpdateBookRequest request) {
//   TODO     поменять на кастомные ексепшины
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        bookMapper.updateBookFromRequest(request, book);

        return bookMapper.toCreateBookResponse(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetCopyOfBookResponse> getAvailableCopiesForBook(Long id) {
        return bookMapper
                .toCopyOfBookResponse(
                        copyBookRepository
                                .findBookCopyEntitiesByBookIdAndAvailable
                                        (id, true));
    }

    @Override
    public GetCopyOfBookResponse createBookCopy(Long id) {
        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

        BookCopyEntity bookCopyEntity = new BookCopyEntity();
        bookCopyEntity.setBook(bookEntity);
        bookCopyEntity.setAvailable(true);
        return bookCopyMapper
                .toCopyResponse(
                        copyBookRepository.save(bookCopyEntity));
    }

    @Transactional
    @Override
    public GetCopyOfBookResponse updateAvailabilityStatus(Long id, Long copyId, UpdateCopyBookStatusRequest request) {
        BookCopyEntity bookCopyEntity = copyBookRepository.findBookCopyEntityByIdAndBookId(copyId, id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Copy not found with id: " + copyId + " for book id: " + id));

        bookCopyEntity.setAvailable(request.getAvailable());
        return bookCopyMapper.toCopyResponse(copyBookRepository.save(bookCopyEntity));
    }
}
