package com.vlmel.library_management_system.mapper;

import com.vlmel.library_management_system.api.response.GetCopyOfBookResponse;
import com.vlmel.library_management_system.model.BookCopyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCopyMapper {
    GetCopyOfBookResponse toCopyResponse(BookCopyEntity entity);
}
