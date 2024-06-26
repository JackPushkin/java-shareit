package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetItemRequestItemDto {

    private Long id;
    private String name;
    private String description;
    private Long requestId;
    private Boolean available;
}
