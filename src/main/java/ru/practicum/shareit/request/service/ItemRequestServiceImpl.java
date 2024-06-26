package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequest addItemRequest(Long userId, ItemRequestDto requestDto) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User id=%d not found", userId)));
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requester);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemRequestDto> getItemRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                        new NotFoundException(String.format("User id=%d not found", userId)));
        List<ItemRequest> itemRequests = user.getRequests();
        return getGetItemRequestDtos(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public GetItemRequestDto getItemRequestsById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User id=%d not found", userId)));
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Request id=%d not found", requestId)));
        List<Item> items = itemRepository.findAllByRequestIdIn(Set.of(request.getId()));
        return ItemRequestMapper.toGetItemRequestDto(request, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetItemRequestDto> getAllAnotherUsersItemRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User id=%d not found", userId)));
        List<ItemRequest> itemRequests =
                requestRepository.findAllByRequestorIdNot(userId, PageRequest.of(from / size, size)).getContent();
        return getGetItemRequestDtos(itemRequests);
    }

    private List<GetItemRequestDto> getGetItemRequestDtos(List<ItemRequest> itemRequests) {
        Set<Long> itemRequestsIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toSet());
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestsIds);
        Map<Long, List<Item>> itemsTable = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return ItemRequestMapper.toGetItemRequestDto(itemRequests, itemsTable);
    }
}
