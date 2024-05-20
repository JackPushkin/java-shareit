package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item, User owner);

    Item getItemById(Long itemId);

    Item updateItem(Item item, Long itemId, Long userId);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(Long userId, String text);

    Item deleteItem(Long itemId, Long userId);
}
