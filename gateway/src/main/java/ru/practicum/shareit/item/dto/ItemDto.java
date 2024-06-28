package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Builder
@ToString
public class ItemDto {
    private Long id;
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String name;
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String description;
    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private Boolean available;
    @Positive
    private Long requestId;
}
