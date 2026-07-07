package com.vlmel.library_management_system.mapper;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookMapperTest {

    private BookMapper bookMapper;

    @BeforeEach
    void setUp() throws Exception {
        var bookCopyMapper = Mappers.getMapper(BookCopyMapper.class);
        var mapperImpl = new BookMapperImpl();

        Field bookCopyMapperField = BookMapperImpl.class.getDeclaredField("bookCopyMapper");
        bookCopyMapperField.setAccessible(true);
        bookCopyMapperField.set(mapperImpl, bookCopyMapper);

        bookMapper = mapperImpl;
    }

    @Test
    void toBookResponse_mapsAllFields() {
        var entity = bookEntity(1L, "Clean Code", "Robert Martin", "9780306406157", 2008);

        var response = bookMapper.toBookResponse(entity);

        assertEquals(1L, response.getId());
        assertEquals("Clean Code", response.getTitle());
        assertEquals("Robert Martin", response.getAuthor());
        assertEquals("9780306406157", response.getIsbn());
        assertEquals(2008, response.getPublishedYear());
    }

    @Test
    void toBookEntity_mapsCreateBookRequest() {
        var request = new CreateBookRequest();
        request.setTitle("Effective Java");
        request.setAuthor("Joshua Bloch");
        request.setIsbn("9780134685991");
        request.setPublishedYear(2018);

        var entity = bookMapper.toBookEntity(request);

        assertEquals("Effective Java", entity.getTitle());
        assertEquals("Joshua Bloch", entity.getAuthor());
        assertEquals("9780134685991", entity.getIsbn());
        assertEquals(2018, entity.getPublishedYear());
        assertNull(entity.getId());
    }

    @Test
    void toCreateBookResponse_mapsAllFields() {
        var entity = bookEntity(3L, "The Pragmatic Programmer", "David Thomas", "9780135957059", 2019);

        var response = bookMapper.toCreateBookResponse(entity);

        assertEquals(3L, response.getId());
        assertEquals("The Pragmatic Programmer", response.getTitle());
        assertEquals("David Thomas", response.getAuthor());
        assertEquals("9780135957059", response.getIsbn());
        assertEquals(2019, response.getPublishedYear());
    }

    @Test
    void toBookDetailsResponse_mapsBookAndCopies() {
        var entity = bookEntity(1L, "Clean Code", "Robert Martin", "9780306406157", 2008);

        var availableCopy = new BookCopyEntity();
        availableCopy.setId(1L);
        availableCopy.setBook(entity);
        availableCopy.setAvailable(true);

        var unavailableCopy = new BookCopyEntity();
        unavailableCopy.setId(2L);
        unavailableCopy.setBook(entity);
        unavailableCopy.setAvailable(false);

        entity.setCopies(List.of(availableCopy, unavailableCopy));

        var response = bookMapper.toBookDetailsResponse(entity);

        assertEquals(1L, response.getId());
        assertEquals("Clean Code", response.getTitle());
        assertEquals("Robert Martin", response.getAuthor());
        assertEquals("9780306406157", response.getIsbn());
        assertEquals(2008, response.getPublishedYear());
        assertEquals(2, response.getCopies().size());
        assertEquals(1L, response.getCopies().get(0).getId());
        assertTrue(response.getCopies().get(0).getAvailable());
        assertEquals(2L, response.getCopies().get(1).getId());
        assertFalse(response.getCopies().get(1).getAvailable());
    }

    @Test
    void toCopyOfBookResponse_mapsListOfCopies() {
        var book = bookEntity(1L, "Clean Code", "Robert Martin", "9780306406157", 2008);

        var copy1 = new BookCopyEntity();
        copy1.setId(1L);
        copy1.setBook(book);
        copy1.setAvailable(true);

        var copy2 = new BookCopyEntity();
        copy2.setId(2L);
        copy2.setBook(book);
        copy2.setAvailable(false);

        var responses = bookMapper.toCopyOfBookResponse(List.of(copy1, copy2));

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertTrue(responses.get(0).getAvailable());
        assertEquals(2L, responses.get(1).getId());
        assertFalse(responses.get(1).getAvailable());
    }

    @Test
    void updateBookFromRequest_updatesOnlyNonNullFields() {
        var entity = bookEntity(1L, "Clean Code", "Robert Martin", "9780306406157", 2008);

        var request = new UpdateBookRequest();
        request.setTitle("Clean Code (2nd Edition)");
        request.setPublishedYear(2010);

        bookMapper.updateBookFromRequest(request, entity);

        assertEquals("Clean Code (2nd Edition)", entity.getTitle());
        assertEquals("Robert Martin", entity.getAuthor());
        assertEquals("9780306406157", entity.getIsbn());
        assertEquals(2010, entity.getPublishedYear());
    }

    @Test
    void updateBookFromRequest_doesNotOverwriteWithNullFields() {
        var entity = bookEntity(1L, "Clean Code", "Robert Martin", "9780306406157", 2008);

        var request = new UpdateBookRequest();
        request.setAuthor("Robert C. Martin");

        bookMapper.updateBookFromRequest(request, entity);

        assertEquals("Clean Code", entity.getTitle());
        assertEquals("Robert C. Martin", entity.getAuthor());
        assertEquals("9780306406157", entity.getIsbn());
        assertEquals(2008, entity.getPublishedYear());
    }

    private static BookEntity bookEntity(Long id, String title, String author, String isbn, int publishedYear) {
        var book = new BookEntity();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublishedYear(publishedYear);
        return book;
    }
}
