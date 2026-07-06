package com.vlmel.library_management_system.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCopyBookStatusRequest {

    @NotNull(message = "Available status is required")
    private Boolean available;
}
