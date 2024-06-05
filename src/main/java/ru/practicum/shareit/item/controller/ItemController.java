package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.addItem(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    @Validated({ValidationMarker.OnUpdate.class})
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @Positive @PathVariable Long itemId,
                              @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.updateItem(ItemMapper.toItem(itemDto), itemId, userId));
    }

    @GetMapping("/{itemId}")
    public GetItemDto getItemById(@Positive @PathVariable Long itemId,
                                  @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<GetItemDto> getUserItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @NotNull @RequestParam(name = "text", required = false) String text) {
        return ItemMapper.toItemDto(itemService.searchItems(userId, text));
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@Positive @PathVariable Long itemId,
                              @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.deleteItem(itemId, userId));
    }

    @PostMapping("/{itemId}/comment")
    @Validated({ ValidationMarker.OnCreate.class })
    public CommentDto addCommentToItem(@Positive @PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody CommentDto commentDto) {
        return CommentMapper.toCommentDto(
                itemService.addCommentToItem(itemId, userId, CommentMapper.toComment(commentDto)));
    }
}
