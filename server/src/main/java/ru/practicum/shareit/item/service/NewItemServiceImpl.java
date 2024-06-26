package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public NewItemServiceImpl(ItemRepository itemRepository,
                              UserRepository userRepository, ItemRequestRepository requestRepository,
                              CommentRepository commentRepository,
                              BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Item addItem(Item item, Long userId, Long requestId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d not found", userId)));
        owner.addItem(item);
        item.setOwner(owner);
        if (requestId != null) {
            ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                    new NotFoundException(String.format("Request with id=%d not found", requestId)));
            item.setRequest(request);
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long itemId, Long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        Item updatedItem = itemRepository.findByIdAndOwnerId(itemId, userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d does not have item with id=%d", userId, itemId)));
        if (item.getName() != null) updatedItem.setName(item.getName());
        if (item.getDescription() != null) updatedItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) updatedItem.setAvailable(item.getAvailable());
        return itemRepository.save(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public GetItemDto getItemById(Long itemId, Long userId) {
        if (userRepository.existsById(userId)) {
            Item item =  itemRepository.findById(itemId).orElseThrow(() ->
                    new NotFoundException(String.format("Item with id=%d not found", itemId)));
            List<Comment> comments = commentRepository.getCommentsByItemIds(List.of(itemId));
            List<Booking> bookings = new ArrayList<>();
            if (userId.equals(item.getOwner().getId()))
                bookings = bookingRepository.findAllByItemIdInAndItemOwnerId(List.of(itemId), userId);
            return ItemMapper.toGetItemDto(item, comments, bookings);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemDto> getUserItems(Long userId, Integer from, Integer size) {
        if (userRepository.existsById(userId)) {
            List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId,
                    PageRequest.of(from / size, size)).getContent();
            List<Long> userItemsIds = userItems.stream()
                    .map(Item::getId)
                    .collect(Collectors.toList());
            List<Comment> comments = commentRepository.getCommentsByItemIds(userItemsIds);
            List<Booking> bookings = bookingRepository.findAllByItemIdInAndItemOwnerId(userItemsIds, userId);
            return ItemMapper.toGetItemDto(userItems, comments, bookings);
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchItems(Long userId, String text, Integer from, Integer size) {
        List<Item> searchedList = new ArrayList<>();
        if (userRepository.existsById(userId)) {
            if (!text.isEmpty()) {
                searchedList = itemRepository.searchItems(text.toLowerCase(),
                        PageRequest.of(from / size, size)).getContent();
            }
        } else {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
        return searchedList;
    }

    @Override
    @Transactional
    public Item deleteItem(Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d not found", userId)));
        Item removedItem = itemRepository.findByIdAndOwnerId(itemId, userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d does not have item with id=%d", userId, itemId)));
        user.getItems().remove(removedItem);
        itemRepository.deleteById(itemId);
        return removedItem;
    }

    @Override
    @Transactional
    public Comment addCommentToItem(Long itemId, Long userId, Comment comment) {
        Booking booking = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).stream().findFirst().orElseThrow(() ->
                new NotAvailableException(String.format("Not available comment for user with id=%d", userId)));
        User user = booking.getBooker();
        Item item = booking.getItem();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}
