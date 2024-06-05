package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(ShortBookingDto booking, Long userId);

    Booking updateBookingStatus(Long bookingId, Long userId, String approved);

    List<Booking> getAllBookingsByUserFilteredByState(String state, Long userId);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getAllBookingsByItemsOwnerFilteredByState(String state, Long userId);
}
