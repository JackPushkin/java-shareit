package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUser(User user, Long userId);

    User deleteUser(Long userId);
}
