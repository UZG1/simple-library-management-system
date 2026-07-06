package com.vlmel.library_management_system.config;

import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import com.vlmel.library_management_system.repository.BookRepository;
import com.vlmel.library_management_system.repository.CopyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final BookRepository bookRepository;
    private final CopyBookRepository copyBookRepository;

    @Bean
    CommandLineRunner loadSampleData() {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            var cleanCode = bookRepository.save(book(
                    "Clean Code",
                    "Robert C. Martin",
                    "9780132350884",
                    2008
            ));
            var effectiveJava = bookRepository.save(book(
                    "Effective Java",
                    "Joshua Bloch",
                    "9780134685991",
                    2018
            ));
            var pragmaticProgrammer = bookRepository.save(book(
                    "The Pragmatic Programmer",
                    "David Thomas",
                    "9780135957059",
                    2019
            ));

            copyBookRepository.saveAll(List.of(
                    copy(cleanCode, true),
                    copy(cleanCode, false),
                    copy(effectiveJava, true),
                    copy(pragmaticProgrammer, true)
            ));
        };
    }

    private static BookEntity book(String title, String author, String isbn, int publishedYear) {
        var book = new BookEntity();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublishedYear(publishedYear);
        return book;
    }

    private static BookCopyEntity copy(BookEntity book, boolean available) {
        var copy = new BookCopyEntity();
        copy.setBook(book);
        copy.setAvailable(available);
        return copy;
    }
}
