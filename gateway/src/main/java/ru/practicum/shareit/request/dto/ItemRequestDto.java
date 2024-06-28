package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Builder
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String description;
}
