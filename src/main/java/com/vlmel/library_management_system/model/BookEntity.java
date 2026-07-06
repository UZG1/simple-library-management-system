package com.vlmel.library_management_system.model;

import com.vlmel.library_management_system.api.validation.Isbn;
import com.vlmel.library_management_system.api.validation.PublishedYear;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Book")
@Data
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, unique = true)
    private String title;

    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;

    @NotBlank(message = "ISBN is required")
    @Isbn
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Published year is required")
    @PublishedYear
    @Column(nullable = false)
    private Integer publishedYear;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<BookCopyEntity> copies = new ArrayList<>();
}
