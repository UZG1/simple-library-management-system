package com.vlmel.library_management_system.repository;

import com.vlmel.library_management_system.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    boolean existsByTitle(String title);

    boolean existsByIsbn(String isbn);

    boolean existsByTitleAndIdNot(String title, Long id);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
