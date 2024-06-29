package ru.practicum.shareit.error_handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final Map<String, String> errors;
}
