package com.vlmel.library_management_system.api.request;

import com.vlmel.library_management_system.api.validation.ValidIsbn;
import com.vlmel.library_management_system.api.validation.ValidPublishedYear;
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
    @ValidIsbn
    private String isbn;

    @NotNull(message = "Published year is required")
    @ValidPublishedYear
    private Integer publishedYear;
}
