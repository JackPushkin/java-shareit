package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import java.util.List;

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
    public List<Booking> getAllBookingsByUserFilteredByState(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
        BookingState bookingState = BookingState.valueOf(state);
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            default:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.valueOf(bookingState.name()));
        }
    }

    @Override
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
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            default:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.valueOf(bookingState.name()));
        }
    }
}
