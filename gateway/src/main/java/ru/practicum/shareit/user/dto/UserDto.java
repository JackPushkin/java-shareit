package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @Email
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    private String email;
    @NotBlank(groups = { ValidationMarker.OnCreate.class })
    @Pattern(regexp = ".*[^ ].*", groups = { ValidationMarker.OnUpdate.class })
    private String name;
}
