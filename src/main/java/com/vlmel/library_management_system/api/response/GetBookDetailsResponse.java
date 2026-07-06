package com.vlmel.library_management_system.api.response;

import java.util.List;

public class GetBookDetailsResponse {
    Long id;
    String title;
    String author;
    String isbn;
    Integer publishedYear;
    List<GetAvailableCopyOfBook> copies;
}
