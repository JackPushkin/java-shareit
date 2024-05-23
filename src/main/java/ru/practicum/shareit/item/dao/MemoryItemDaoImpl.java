package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class MemoryItemDaoImpl implements ItemDao {

    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item addItem(Item item, User owner) {
        if (!items.containsKey(owner.getId())) {
            items.put(owner.getId(), new HashMap<>());
        }
        item.setId(generateId());
        item.setOwner(owner);
        items.get(owner.getId()).put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.values().stream()
                .filter(longItemMap -> longItemMap.containsKey(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Предмет id=%d не найден.", itemId)))
                .get(itemId);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        checkUserId(userId);
        Item updatedItem = items.get(userId).get(itemId);
        if (item.getName() != null) updatedItem.setName(item.getName());
        if (item.getDescription() != null) updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) updatedItem.setAvailable(item.getAvailable());
        return items.get(userId).put(itemId, updatedItem);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        checkUserId(userId);
        return new ArrayList<>(items.get(userId).values());
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        checkUserId(userId);
        String lowerText = text.toLowerCase();
        List<Item> searchedList = new ArrayList<>();
        if (!text.isEmpty()) {
            searchedList = items.values().stream()
                    .flatMap((Function<Map<Long, Item>, Stream<Item>>) longItemMap -> longItemMap.values().stream())
                    .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                            item.getDescription().toLowerCase().contains(lowerText))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
        return searchedList;
    }

    @Override
    public Item deleteItem(Long itemId, Long userId) {
        checkUserId(userId);
        return items.get(userId).remove(itemId);
    }

    private void checkUserId(Long userId) {
        if (!items.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден.", userId));
        }
    }

    private Long generateId() {
        return id++;
    }
}
