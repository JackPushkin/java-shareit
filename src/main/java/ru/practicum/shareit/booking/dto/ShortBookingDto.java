package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.validation.ValidationMarker;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ShortBookingDto {

    private Long id;

    @Future
    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private LocalDateTime start;

    @Future
    @NotNull(groups = { ValidationMarker.OnCreate.class })
    private LocalDateTime end;

    private Long itemId;
    private Long bookerId;
}
