package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> addItemRequest(
            @Valid @RequestBody ItemRequestDto requestDto,
            @Positive(groups = {ValidationMarker.OnCreate.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add request by user with id={}", userId);
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get requests by user with id={}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Positive @PathVariable Long requestId) {
        log.info("Get request with id={} by user with id={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAnotherUsersItemRequests(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests. Parameters: userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllAnotherUsersItemRequests(userId, from, size);
    }
}
