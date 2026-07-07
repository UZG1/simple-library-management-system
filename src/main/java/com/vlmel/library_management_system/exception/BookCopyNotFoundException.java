package com.vlmel.library_management_system.exception;

public class BookCopyNotFoundException extends RuntimeException {

    public BookCopyNotFoundException(Long copyId, Long bookId) {
        super("Copy not found with id: " + copyId + " for book id: " + bookId);
    }
}
