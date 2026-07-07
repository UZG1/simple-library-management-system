package com.vlmel.library_management_system.mapper;

import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookCopyMapperTest {

    private BookCopyMapper bookCopyMapper;

    @BeforeEach
    void setUp() {
        bookCopyMapper = Mappers.getMapper(BookCopyMapper.class);
    }

    @Test
    void toCopyResponse_mapsIdAndAvailability() {
        var book = new BookEntity();
        book.setId(1L);

        var copy = new BookCopyEntity();
        copy.setId(10L);
        copy.setBook(book);
        copy.setAvailable(true);

        var response = bookCopyMapper.toCopyResponse(copy);

        assertEquals(10L, response.getId());
        assertTrue(response.getAvailable());
    }

    @Test
    void toCopyResponse_mapsUnavailableCopy() {
        var copy = new BookCopyEntity();
        copy.setId(2L);
        copy.setAvailable(false);

        var response = bookCopyMapper.toCopyResponse(copy);

        assertEquals(2L, response.getId());
        assertFalse(response.getAvailable());
    }
}
