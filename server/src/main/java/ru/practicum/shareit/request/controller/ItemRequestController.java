package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addItemRequest(
            @RequestBody ItemRequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(userId, requestDto));
    }

    @GetMapping
    public List<GetItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.getItemRequestsById(userId, requestId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllAnotherUsersItemRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllAnotherUsersItemRequests(userId, from, size);
    }
}
