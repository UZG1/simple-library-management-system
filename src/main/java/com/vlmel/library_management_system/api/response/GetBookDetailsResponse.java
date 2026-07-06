package com.vlmel.library_management_system.api.response;

import lombok.Data;

import java.util.List;

@Data
public class GetBookDetailsResponse {
    Long id;
    String title;
    String author;
    String isbn;
    Integer publishedYear;
    List<GetCopyOfBookResponse> copies;
}
