package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    @Validated({ ValidationMarker.OnCreate.class })
    public BookingDto addBooking(@Valid @RequestBody ShortBookingDto bookingDto,
                                 @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@Positive @PathVariable Long bookingId,
                                          @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam("approved") String approved) {
        return BookingMapper.toBookingDto(bookingService.updateBookingStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@Positive @PathVariable Long bookingId,
                                     @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.getAllBookingsByUserFilteredByState(state, userId));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByItemsOwnerFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.getAllBookingsByItemsOwnerFilteredByState(state, userId));
    }
}
