package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private Long id;

    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String name;

    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String description;

    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private Boolean available;

    private User owner;

    private ItemRequest request;
}
