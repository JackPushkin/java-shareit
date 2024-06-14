package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

public interface BookingTime {

    LocalDateTime getStart();

    LocalDateTime getEnd();
}
