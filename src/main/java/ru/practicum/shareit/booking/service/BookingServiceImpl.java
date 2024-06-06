package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Booking addBooking(ShortBookingDto bookingDto, Long userId) {
        LocalDateTime startTime = bookingDto.getStart();
        LocalDateTime endTime = bookingDto.getEnd();
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new ConstraintViolationException("Incorrect endTime parameter", null);
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException(String.format("Item with id=%d not found", bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new NotAvailableException(String.format("Item with id=%d not available", item.getId()));
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Item with id=%d suitable for booking not found", item.getId()));
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d not found", userId)));
        Booking booking = BookingMapper.toBooking(bookingDto, item, user, BookingStatus.WAITING);
        item.getBookings().add(booking);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long bookingId, Long userId, String approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException(String.format(
                    "Status change is not available for booking with id=%d", bookingId));
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            boolean isApproved = Boolean.parseBoolean(approved);
            booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            return bookingRepository.save(booking);
        } else {
            throw new NotFoundException(String.format(
                    "User with id=%d has no booking with id=%d", userId, bookingId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Booking with id=%d not found", bookingId)));
        Long bookerId = booking.getBooker().getId();
        Long itemOwnerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !itemOwnerId.equals(userId)) {
            throw new NotFoundException(String.format(
                    "User with id=%d has no booking with id=%d", userId, bookerId));
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByUserFilteredByState(String state, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%d not found", userId)));
        BookingState bookingState = BookingState.valueOf(state);
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByItemId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByItemId(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByItemId(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByItemId(
                        userId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByItemId(
                        userId, BookingStatus.valueOf(bookingState.name()));
        }
        List<Long> bookingsIds = bookings.stream().map(Booking::getId).collect(Collectors.toList());
        List<Item> bookingItems = itemRepository.findItemsByBookingsIdInOrderById(bookingsIds);
        for (int i = 0; i < bookings.size(); i++) {
            bookings.get(i).setItem(bookingItems.get(i));
            bookings.get(i).setBooker(user);
        }
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed().thenComparing(Booking::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookingsByItemsOwnerFilteredByState(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
        BookingState bookingState = BookingState.valueOf(state);
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
            default:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.valueOf(bookingState.name()));
        }
    }
}