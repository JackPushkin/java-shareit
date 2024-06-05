package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId, Long userId);

    GetItemDto getItemById(Long itemId, Long userId);

    List<GetItemDto> getUserItems(Long userId);

    List<Item> searchItems(Long userId, String text);

    Item deleteItem(Long itemId, Long userId);

    Comment addCommentToItem(Long itemId, Long userId, Comment comment);
}
