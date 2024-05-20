package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyAddedException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class MemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User addUser(User user) {
        alreadyAddedCheck(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        checkUser(userId);
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User updatedUser) {
        alreadyAddedCheck(updatedUser);
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    @Override
    public User deleteUser(Long userId) {
        checkUser(userId);
        return users.remove(userId);
    }

    private void checkUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
    }

    private void alreadyAddedCheck(User user) {
        boolean isAlreadyAdded = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()));
        if (isAlreadyAdded) {
            throw new UserAlreadyAddedException(
                    String.format("Ошибка. Пользователь с email=%s уже существует.",
                    user.getEmail()));
        }
    }

    private Long generateId() {
        return id++;
    }
}
