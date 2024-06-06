package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class NewUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public NewUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User id=%d not found", userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user, Long userId) {
        User updatedUser = getUserById(userId);
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        if (user.getName() != null) updatedUser.setName(user.getName());
        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public User deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.deleteById(userId);
        return user;
    }
}
