package ru.practicum.shareit.exception;

public class UserAlreadyAddedException extends RuntimeException {

    public UserAlreadyAddedException(String message) {
        super(message);
    }
}
