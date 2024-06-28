package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.item.dto.GetItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetItemRequestDto {
    private Long id;
    private LocalDateTime created;
    private String description;
    private List<GetItemRequestItemDto> items;
}
