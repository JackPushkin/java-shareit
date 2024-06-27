package ru.practicum.shareit.booking_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void addBookingTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.addBooking(any(), anyLong())).thenReturn(getBooking(booking, BookingStatus.WAITING));
        // Check endpoint
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2030-01-01T10:00:00")))
                .andExpect(jsonPath("$.end", is("2030-01-11T10:00:00")))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    public void addBookingWithoutIdHeaderTest() throws Exception {
        // Create booking
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        // Check endpoint
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addBookingWithDateInPassTest() throws Exception {
        // Create booking
        ShortBookingDto booking = getBooking("2000-01-01 10:00:00");
        // Check endpoint
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addBookingWithNotFoundUserIdTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.addBooking(any(), anyLong())).thenThrow(new NotFoundException("Item with id=%d not found"));
        // Check endpoint
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    public void updateBookingStatusTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(getBooking(booking, BookingStatus.APPROVED));
        // Check endpoint
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2030-01-01T10:00:00")))
                .andExpect(jsonPath("$.end", is("2030-01-11T10:00:00")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    public void updateBookingStatusWithIncorrectBookingIdTest() throws Exception {
        // Create incorrect booking id
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(patch("/bookings/" + incorrectId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void updateBookingStatusWithIncorrectUserIdTest() throws Exception {
        // Check endpoint
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", -100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void updateBookingStatusWithoutAvailableParamTest() throws Exception {
        // Check endpoint
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getBookingByIdTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(getBooking(booking, BookingStatus.WAITING));
        // Check endpoint
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2030-01-01T10:00:00")))
                .andExpect(jsonPath("$.end", is("2030-01-11T10:00:00")))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    public void getBookingByIdWithIncorrectBookingIdTest() throws Exception {
        // Create incorrect booking id
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/bookings/" + incorrectId)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getBookingByIdWithIncorrectUserIdTest() throws Exception {
        // Create incorrect booking id
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", incorrectId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByUserFilteredByStateTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.getAllBookingsByUserFilteredByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(getBooking(booking, BookingStatus.WAITING)));
        // Check endpoint
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void getAllBookingsByUserFilteredByState_WithoutStateParam_Test() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.getAllBookingsByUserFilteredByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(getBooking(booking, BookingStatus.WAITING)));
        // Check endpoint
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void getAllBookingsByUserFilteredByState_WithIncorrecrFromParam_Test() throws Exception {
        // Create incorrect from value
        String incorrectFromValue = "-100";
        // Check endpoint
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", incorrectFromValue)
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByUserFilteredByState_WithIncorrecrSizeParam_Test() throws Exception {
        // Create incorrect from value
        String incorrectSizeValue = "-100";
        // Check endpoint
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", incorrectSizeValue)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByUserFilteredByState_WithIncorrecrUserId_Test() throws Exception {
        // Create incorrect from value
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", incorrectId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByStateTest() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.getAllBookingsByItemsOwnerFilteredByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(getBooking(booking, BookingStatus.WAITING)));
        // Check endpoint
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByState_WithoutStateParam_Test() throws Exception {
        // Create booking. Setup mock
        ShortBookingDto booking = getBooking("2030-01-01 10:00:00");
        when(bookingService.getAllBookingsByItemsOwnerFilteredByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(getBooking(booking, BookingStatus.WAITING)));
        // Check endpoint
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByState_WithIncorrecrFromParam_Test() throws Exception {
        // Create incorrect from value
        String incorrectFromValue = "-100";
        // Check endpoint
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", incorrectFromValue)
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByState_WithIncorrecrSizeParam_Test() throws Exception {
        // Create incorrect from value
        String incorrectSizeValue = "-100";
        // Check endpoint
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", incorrectSizeValue)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByState_WithIncorrecrUserId_Test() throws Exception {
        // Create incorrect from value
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", incorrectId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    private ShortBookingDto getBooking(String startTime) {
        return ShortBookingDto.builder()
                .id(1L)
                .start(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .end(LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .plus(Duration.ofDays(10)))
                .itemId(1L)
                .build();
    }

    private Booking getBooking(ShortBookingDto bookingDto, BookingStatus status) {
        return Booking.builder()
                .id(bookingDto.getId())
                .item(Item.builder().id(bookingDto.getItemId()).build())
                .booker(User.builder().build())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(status)
                .build();
    }
}
