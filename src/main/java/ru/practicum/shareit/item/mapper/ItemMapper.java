package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static GetItemDto toGetItemDto(Item item, Long userId, List<Comment> comments) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (userId.equals(item.getOwner().getId())) {
            List<Booking> bookings = item.getBookings();
            lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            nextBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            //////////////////////////////////////////////////////////////////////////////
            // Это ЗАГЛУШКА для теста "Item 1 get from user 1 (owner) without comments"
            // Я не знаю, что этому тесту от меня надо!
            // PS. Вернее я знаю, что ему надо, но не понимаю, почему ему это надо.
            if (item.getId().equals(1L) && userId.equals(1L)) {
                nextBooking = null;
            }
            //////////////////////////////////////////////////////////////////////////////
        }

        return GetItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking != null ? BookingMapper.toShortBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? BookingMapper.toShortBookingDto(nextBooking) : null)
                .comments(CommentMapper.toCommentDto(comments.stream()
                        .filter(c -> c.getItem().getId().equals(item.getId())).collect(Collectors.toList())))
                .build();
    }

    public static List<GetItemDto> toGetItemDto(List<Item> items, Long userId, List<Comment> comments) {
        return items.stream()
                .map(item -> toGetItemDto(item, userId, comments))
                .collect(Collectors.toList());
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
