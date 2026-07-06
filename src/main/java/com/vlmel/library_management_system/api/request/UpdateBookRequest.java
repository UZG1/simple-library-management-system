package com.vlmel.library_management_system.api.request;

import com.vlmel.library_management_system.api.validation.ValidIsbn;
import com.vlmel.library_management_system.api.validation.ValidPublishedYear;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateBookRequest {

    @NotBlank(message = "Title must not be blank")
    private String title;

    @NotBlank(message = "Author must not be blank")
    private String author;

    @ValidIsbn
    private String isbn;

    @ValidPublishedYear
    private Integer publishedYear;
}
