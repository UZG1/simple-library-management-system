package com.vlmel.library_management_system.repository;

import com.vlmel.library_management_system.model.BookCopyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CopyBookRepository extends JpaRepository<BookCopyEntity, Long> {
    List<BookCopyEntity> findBookCopyEntitiesByBookIdAndAvailable(Long id, Boolean available);

    Optional<BookCopyEntity> findBookCopyEntityByIdAndBookId(Long id, Long bookId);
}
