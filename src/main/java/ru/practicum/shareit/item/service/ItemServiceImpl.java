package ru.practicum.shareit.item.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public Item addItem(Item item, Long userId) {
        User owner = userDao.getUserById(userId);
        return itemDao.addItem(item, owner);
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        return itemDao.updateItem(item, itemId, userId);
    }

    @Override
    public Item getItemById(Long itemId, Long userId) {
        return itemDao.getItemById(itemId);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return itemDao.getUserItems(userId);
    }

    @Override
    public List<Item> searchItems(Long userId, String text) {
        return itemDao.searchItems(userId, text);
    }

    @Override
    public Item deleteItem(Long itemId, Long userId) {
        return itemDao.deleteItem(itemId, userId);
    }
}
