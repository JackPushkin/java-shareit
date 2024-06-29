package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    private String description;
}
