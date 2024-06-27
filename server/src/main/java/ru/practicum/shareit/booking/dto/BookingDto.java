package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class BookingDto {

    private Long id;

    @Future
    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private LocalDateTime start;

    @Future
    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private LocalDateTime end;

    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
