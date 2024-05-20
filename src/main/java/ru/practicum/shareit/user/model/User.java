package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {

    private Long id;

    @Email
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    private String email;

    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String name;
}
