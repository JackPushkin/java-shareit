package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);

    User getUserById(Long userId);

    List<User> getAllUsers();

    User updateUser(User updatedUser);

    User deleteUser(Long userId);
}
