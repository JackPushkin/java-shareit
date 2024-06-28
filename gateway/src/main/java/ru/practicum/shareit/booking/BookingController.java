package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> addBooking(
            @Valid @RequestBody ShortBookingDto bookingDto,
            @Positive(groups = {ValidationMarker.OnCreate.class}) @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Add booking with itemId={} by user with id={}", bookingDto.getItemId(), userId);
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @Positive @PathVariable Long bookingId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("approved") String approved) {
        log.info("Update status on approved={}", approved);
        return bookingClient.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @Positive @PathVariable Long bookingId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking with id={} by user with id={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get bookings by user with id={}. Parameters: state={}, from={}, size={}", userId, state, from, size);
        return bookingClient.getAllBookingsByUserFilteredByState(state, from, size, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByItemsOwnerFilteredByState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get bookings by items owner with id={}. Parameters: state={}, from={}, size={}",
                userId, state, from, size);
        return bookingClient.getAllBookingsByItemsOwnerFilteredByState(state, from, size, userId);
    }
}
