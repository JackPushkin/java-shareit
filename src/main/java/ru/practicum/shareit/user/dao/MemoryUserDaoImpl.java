package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyAddedException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class MemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmails = new HashSet<>();
    private Long id = 1L;

    @Override
    public User addUser(User user) {
        alreadyAddedCheck(user.getEmail(), user.getId());
        user.setId(generateId());
        users.put(user.getId(), user);
        usersEmails.add(user.getEmail());
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
    public User updateUser(User user, Long userId) {
        checkUser(userId);
        alreadyAddedCheck(user.getEmail(), userId);
        User updatedUser = getUserById(userId);
        usersEmails.remove(updatedUser.getEmail());
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        if (user.getName() != null) updatedUser.setName(user.getName());
        users.put(updatedUser.getId(), updatedUser);
        usersEmails.add(updatedUser.getEmail());
        return updatedUser;
    }

    @Override
    public User deleteUser(Long userId) {
        checkUser(userId);
        usersEmails.remove(users.get(userId).getEmail());
        return users.remove(userId);
    }

    private void checkUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
    }

    private void alreadyAddedCheck(String email, Long userId) {
        boolean isAlreadyAdded =
                usersEmails.contains(email) && (users.get(userId) == null || !users.get(userId).getEmail().equals(email));

        if (isAlreadyAdded) {
            throw new UserAlreadyAddedException(
                    String.format("Ошибка. Пользователь с email=%s уже существует.", email));
        }
    }

    private Long generateId() {
        return id++;
    }
}
