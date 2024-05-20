package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated({ ValidationMarker.OnCreate.class })
    public UserDto addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@Positive @PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    @Validated({ ValidationMarker.OnUpdate.class })
    public UserDto updateUser(@Valid @RequestBody User user, @Positive @PathVariable Long userId) {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@Positive @PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
