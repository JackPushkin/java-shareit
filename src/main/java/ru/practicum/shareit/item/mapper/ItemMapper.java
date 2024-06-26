package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.GetItemRequestItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static GetItemRequestItemDto toGetItemRequestItemDto(Item item) {
        return GetItemRequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequest().getId())
                .available(item.getAvailable())
                .build();
    }

    public static List<GetItemRequestItemDto> toGetItemRequestItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toGetItemRequestItemDto)
                .collect(Collectors.toList());
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static GetItemDto toGetItemDto(Item item, List<Comment> comments, List<Booking> bookings) {
        Booking lastBooking;
        Booking nextBooking;

        lastBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        booking.getStatus() == BookingStatus.APPROVED)
                .max(Comparator.comparing(Booking::getStart))
                .orElse(null);
        nextBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                        booking.getStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);

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

    public static List<GetItemDto> toGetItemDto(List<Item> items, List<Comment> comments, List<Booking> bookings) {
        return items.stream()
                .map(item -> toGetItemDto(item, comments, bookings))
                .collect(Collectors.toList());
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static List<Item> toItem(List<ItemDto> itemDtos) {
        return itemDtos.stream()
                .map(ItemMapper::toItem)
                .collect(Collectors.toList());
    }
}
