package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addItemRequest(Long userId, ItemRequestDto requestDto);

    List<GetItemRequestDto> getItemRequests(Long userId);

    GetItemRequestDto getItemRequestsById(Long userId, Long requestId);

    List<GetItemRequestDto> getAllAnotherUsersItemRequests(Long userId, Integer from, Integer size);
}
