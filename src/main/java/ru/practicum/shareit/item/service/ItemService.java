package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item, Long userId);

    Item updateItem(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId, Long userId);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(Long userId, String text);

    Item deleteItem(Long itemId, Long userId);
}
