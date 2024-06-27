package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.addUser(UserMapper.toUser(userDto)));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@Positive @PathVariable Long userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(userService.getAllUsers());
    }

    @PatchMapping("/{userId}")
    @Validated({ ValidationMarker.OnUpdate.class })
    public UserDto updateUser(@Valid @RequestBody UserDto userDto,
                              @Positive(groups = {ValidationMarker.OnUpdate.class}) @PathVariable Long userId) {
        return UserMapper.toUserDto(userService.updateUser(UserMapper.toUser(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@Positive @PathVariable Long userId) {
        return UserMapper.toUserDto(userService.deleteUser(userId));
    }
}