package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.addUser(UserMapper.toUser(userDto)));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(userService.getAllUsers());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return UserMapper.toUserDto(userService.updateUser(UserMapper.toUser(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.deleteUser(userId));
    }
}
