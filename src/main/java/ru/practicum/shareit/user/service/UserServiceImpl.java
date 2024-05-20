package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto addUser(User user) {
        return UserMapper.toUserDto(userDao.addUser(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userDao.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(userDao.getAllUsers());
    }

    @Override
    public UserDto updateUser(User user, Long userId) {
        User updatedUser = UserMapper.toUser(getUserById(userId));
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        if (user.getName() != null) updatedUser.setName(user.getName());
        return UserMapper.toUserDto(userDao.updateUser(updatedUser));
    }

    @Override
    public UserDto deleteUser(Long userId) {
        return UserMapper.toUserDto(userDao.deleteUser(userId));
    }
}
