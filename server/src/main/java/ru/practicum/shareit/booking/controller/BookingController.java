package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(
            @RequestBody ShortBookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("approved") String approved) {
        return BookingMapper.toBookingDto(bookingService.updateBookingStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(
                bookingService.getAllBookingsByUserFilteredByState(state, userId, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByItemsOwnerFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(
                bookingService.getAllBookingsByItemsOwnerFilteredByState(state, userId, from, size));
    }
}
