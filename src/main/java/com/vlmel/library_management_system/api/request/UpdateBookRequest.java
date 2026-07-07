package com.vlmel.library_management_system.api.request;

import com.vlmel.library_management_system.api.validation.Isbn;
import com.vlmel.library_management_system.api.validation.PublishedYear;
import lombok.Data;

@Data
public class UpdateBookRequest {

    private String title;

    private String author;

    @Isbn
    private String isbn;

    @PublishedYear
    private Integer publishedYear;
}
