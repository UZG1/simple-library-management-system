package com.vlmel.library_management_system.service.impl;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateCopyBookStatusRequest;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.exception.BookIsbnAlreadyExistsException;
import com.vlmel.library_management_system.exception.BookCopyNotFoundException;
import com.vlmel.library_management_system.exception.BookNotFoundException;
import com.vlmel.library_management_system.exception.BookTitleAlreadyExistsException;
import com.vlmel.library_management_system.mapper.BookCopyMapper;
import com.vlmel.library_management_system.mapper.BookMapper;
import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import com.vlmel.library_management_system.repository.CopyBookRepository;
import com.vlmel.library_management_system.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<GetBookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toBookResponse);
    }

    @Override
    public GetBookWithoutCopiesResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByTitle(request.getTitle())) {
            throw new BookTitleAlreadyExistsException(request.getTitle());
        }
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookIsbnAlreadyExistsException(request.getIsbn());
        }

        BookEntity bookEntity = bookRepository.save(
                bookMapper.toBookEntity(request)
        );
        return bookMapper.toCreateBookResponse(bookEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public GetBookDetailsResponse getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookDetailsResponse)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    @Override
    public GetBookWithoutCopiesResponse updateBook(Long id, UpdateBookRequest request) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (request.getTitle() != null && bookRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
            throw new BookTitleAlreadyExistsException(request.getTitle());
        }
        if (request.getIsbn() != null && bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
            throw new BookIsbnAlreadyExistsException(request.getIsbn());
        }

        bookMapper.updateBookFromRequest(request, book);

        return bookMapper.toCreateBookResponse(bookRepository.save(book));
    }

    @Transactional
    @Override
    public void deleteBookById(Long id) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GetCopyOfBookResponse> getAvailableCopiesForBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        return bookMapper
                .toCopyOfBookResponse(
                        copyBookRepository
                                .findBookCopyEntitiesByBookIdAndAvailable
                                        (id, true));
    }

    @Override
    public GetCopyOfBookResponse createBookCopy(Long id) {
        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

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
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        BookCopyEntity bookCopyEntity = copyBookRepository.findBookCopyEntityByIdAndBookId(copyId, id)
                .orElseThrow(() -> new BookCopyNotFoundException(copyId, id));

        bookCopyEntity.setAvailable(request.getAvailable());
        return bookCopyMapper.toCopyResponse(copyBookRepository.save(bookCopyEntity));
    }
}
