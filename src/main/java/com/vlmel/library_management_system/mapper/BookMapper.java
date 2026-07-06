package com.vlmel.library_management_system.mapper;

import com.vlmel.library_management_system.api.request.CreateBookRequest;
import com.vlmel.library_management_system.api.response.CreateBookResponse;
import com.vlmel.library_management_system.api.response.GetBookResponse;
import com.vlmel.library_management_system.model.BookEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    GetBookResponse toBookResponse(BookEntity entity);

    BookEntity toBookEntity(CreateBookRequest dto);

    CreateBookResponse toCreateBookResponse(BookEntity entity);

}
