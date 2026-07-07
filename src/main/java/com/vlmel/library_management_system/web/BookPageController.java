package com.vlmel.library_management_system.web;

import com.vlmel.library_management_system.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BookPageController {

    private final BookService bookService;

    @GetMapping("/")
    public String listBooks(Model model) {
        var books = bookService.getAllBooks(
                PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"))
        ).getContent();
        model.addAttribute("books", books);
        return "books";
    }
}
