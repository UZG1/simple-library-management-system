package com.vlmel.library_management_system.repository;

import com.vlmel.library_management_system.model.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CopyBookRepository extends JpaRepository<BookCopyEntity, Long> {
}
