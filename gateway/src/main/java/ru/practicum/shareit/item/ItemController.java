package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> addItem(
            @Valid @RequestBody ItemDto itemDto,
            @Positive(groups = {ValidationMarker.OnCreate.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add item with name={}, description={}, available={}, reauestId={}",
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId());
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({ValidationMarker.OnUpdate.class})
    public ResponseEntity<Object> updateItem(
            @Valid @RequestBody ItemDto itemDto,
            @Positive(groups = {ValidationMarker.OnUpdate.class}) @PathVariable Long itemId,
            @Positive(groups = {ValidationMarker.OnUpdate.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update item with id={}", itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @NotNull @RequestParam(value = "text", required = false) String text,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItems(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @Validated({ ValidationMarker.OnCreate.class })
    public ResponseEntity<Object> addCommentToItem(
            @Positive(groups = {ValidationMarker.OnCreate.class}) @PathVariable Long itemId,
            @Positive(groups = {ValidationMarker.OnCreate.class}) @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addCommentToItem(userId, itemId, commentDto);
    }
}
