package com.vlmel.library_management_system.exception;

public class BookIsbnAlreadyExistsException extends RuntimeException {

    public BookIsbnAlreadyExistsException(String isbn) {
        super("Book with ISBN '" + isbn + "' already exists");
    }
}
