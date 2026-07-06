package com.vlmel.library_management_system.api.request;

import com.vlmel.library_management_system.api.validation.Isbn;
import com.vlmel.library_management_system.api.validation.PublishedYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Isbn
    private String isbn;

    @NotNull(message = "Published year is required")
    @PublishedYear
    private Integer publishedYear;
}
