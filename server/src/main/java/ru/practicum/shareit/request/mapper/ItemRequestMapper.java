package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static GetItemRequestDto toGetItemRequestDto(ItemRequest itemRequest,
                                                        List<Item> items) {
        return GetItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items != null ? ItemMapper.toGetItemRequestItemDto(items) : new ArrayList<>())
                .build();
    }

    public static List<GetItemRequestDto> toGetItemRequestDto(List<ItemRequest> itemRequests,
                                                              Map<Long, List<Item>> allItems) {
        return itemRequests.stream()
                .map(itemRequest -> toGetItemRequestDto(itemRequest, allItems.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .created(itemRequestDto.getCreated())
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .build();
    }
}
