package com.vlmel.library_management_system.mapper;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.request.UpdateBookRequest;
import com.vlmel.library_management_system.api.response.GetBookWithoutCopiesResponse;
import com.vlmel.library_management_system.api.response.GetBookDetailsResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.model.BookCopyEntity;
import com.vlmel.library_management_system.model.BookEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.util.List;

@Mapper(componentModel = "spring", uses = BookCopyMapper.class)
public interface BookMapper {
    GetBookResponse toBookResponse(BookEntity entity);

    BookEntity toBookEntity(CreateBookRequest dto);

    GetBookWithoutCopiesResponse toCreateBookResponse(BookEntity entity);

    GetBookDetailsResponse toBookDetailsResponse(BookEntity entity);

    List<GetCopyOfBookResponse> toCopyOfBookResponse(List<BookCopyEntity> copies);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBookFromRequest(UpdateBookRequest request, @MappingTarget BookEntity entity);
}
