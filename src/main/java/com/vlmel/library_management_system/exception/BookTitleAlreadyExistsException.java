package com.vlmel.library_management_system.exception;

public class BookTitleAlreadyExistsException extends RuntimeException {

    public BookTitleAlreadyExistsException(String title) {
        super("Book with title '" + title + "' already exists");
    }
}
