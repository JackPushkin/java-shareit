package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ItemRequestDto addItemRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                         @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(userId, requestDto));
    }

    @GetMapping
    public List<GetItemRequestDto> getUserItemRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getItemRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Positive @PathVariable Long requestId) {
        return itemRequestService.getItemRequestsById(userId, requestId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllAnotherUsersItemRequests(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", required = false) Integer size) {
        return itemRequestService.getAllAnotherUsersItemRequests(userId, from, size);
    }
}
