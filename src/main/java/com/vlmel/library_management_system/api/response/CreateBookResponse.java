package com.vlmel.library_management_system.api.response;

import lombok.Data;

@Data
public class CreateBookResponse {
    Long id;
    String title;
    String author;
    String isbn;
    Integer publishedYear;
}
