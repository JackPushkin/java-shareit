package ru.practicum.shareit.booking_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingIntegrationTest {

    private final BookingService bookingService;
    private final EntityManager em;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss");

    @Test
    public void addBookingTest() {
        // Create itemOwner and booker. Add them to DB
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Long itemOwnerId = addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item. Add item to DB
        Item item = createItem(1, itemOwner);
        addItemToDb(item);
        // Create bookings
        Booking newBooking1 = createBooking("2030-01-01 | 10:00:00", "2030-01-01 | 12:00:00", item, booker);
        Booking newBooking2 = createBooking("2040-01-01 | 10:00:00", "2040-01-01 | 12:00:00", item, booker);

        // Add booking to DB
        Booking booking = bookingService.addBooking(BookingMapper.toShortBookingDto(newBooking1), bookerId);
        // Get booking from DB
        Booking bookingFromDb = getBookingFromDb(booking.getId());
        // Check results
        assertThat(bookingFromDb.getId(), equalTo(booking.getId()));
        assertThat(bookingFromDb.getStart(), equalTo(newBooking1.getStart()));
        assertThat(bookingFromDb.getEnd(), equalTo(newBooking1.getEnd()));
        assertThat(bookingFromDb.getStatus(), equalTo(newBooking1.getStatus()));
        assertThat(bookingFromDb.getItem().getId(), equalTo(newBooking1.getItem().getId()));
        assertThat(bookingFromDb.getBooker().getId(), equalTo(newBooking1.getBooker().getId()));

        // Try to add booking with start after end
        Booking newBookingWithIncorrectStart =
                createBooking("2040-01-01 | 10:00:00", "2035-01-01 | 12:00:00", item, booker);
        assertThrows(NotAvailableException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBookingWithIncorrectStart), bookerId));

        // Try to add booking with incorrect item id
        Item itemWithIncorrectId = createItem(2, itemOwner);
        itemWithIncorrectId.setId(-100L);
        Booking newBookingWithIncorrectItemId =
                createBooking("2040-01-01 | 10:00:00", "2040-01-01 | 12:00:00", itemWithIncorrectId, booker);
        assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBookingWithIncorrectItemId), bookerId));

        // Try to add booking with incorrect booker id
        assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBooking2), 100L));

        // Try to add booking by item owner
        assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBooking2), itemOwnerId));

        // Try to add booking with item available = false
        Item itemWithFalseAvailable = createItem(3, itemOwner);
        itemWithFalseAvailable.setAvailable(false);
        addItemToDb(itemWithFalseAvailable);
        Booking newBookingWithFalseItemAvailable =
                createBooking("2050-01-01 | 10:00:00", "2050-01-01 | 12:00:00", itemWithFalseAvailable, booker);
        assertThrows(NotAvailableException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBookingWithFalseItemAvailable), itemOwnerId));

        // Try to add booking with time intersection with newBooking1
        em.createQuery("UPDATE Booking AS b SET b.status = 'APPROVED'").executeUpdate();
        Booking newBookingWithTimeIntersection = createBooking(
                newBooking1.getStart().format(formatter), newBooking1.getEnd().format(formatter), item, booker);
        assertThrows(NotAvailableException.class, () ->
                bookingService.addBooking(BookingMapper.toShortBookingDto(newBookingWithTimeIntersection), bookerId));
    }

    @Test
    public void updateBookingStatusTest() {
        // Create itemOwner and booker. Add them to DB
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Long itemOwnerId = addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item. Add item to DB
        Item item = createItem(1, itemOwner);
        addItemToDb(item);
        // Create booking. Add booking to DB
        Booking newBooking = createBooking("2030-01-01 | 10:00:00", "2030-01-01 | 12:00:00", item, booker);
        Booking addedBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(newBooking), bookerId);

        // Try to update booking status with incorrect booking id
        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(100L, itemOwnerId, "true"));

        // Try to update booking not by item owner
        assertThrows(NotFoundException.class,
                () -> bookingService.updateBookingStatus(addedBooking.getId(), bookerId, "true"));

        // Update booking status
        Booking bookingFromDb = getBookingFromDb(addedBooking.getId());
        assertThat(bookingFromDb.getStatus(), is(BookingStatus.WAITING));
        bookingService.updateBookingStatus(addedBooking.getId(), itemOwnerId, "true");
        bookingFromDb = getBookingFromDb(addedBooking.getId());
        assertThat(bookingFromDb.getStatus(), is(BookingStatus.APPROVED));

        // Try to update already approved booking
        assertThrows(NotAvailableException.class,
                () -> bookingService.updateBookingStatus(addedBooking.getId(), itemOwnerId, "false"));
    }

    @Test
    public void getBookingByIdTest() {
        // Create itemOwner and booker. Add them to DB
        User itemOwner = createUser(1);
        User booker = createUser(2);
        addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item. Add item to DB
        Item item = createItem(1, itemOwner);
        addItemToDb(item);
        // Create booking. Add booking to DB
        Booking newBooking = createBooking("2030-01-01 | 10:00:00", "2030-01-01 | 12:00:00", item, booker);
        Booking addedBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(newBooking), bookerId);

        // Get booking from Db
        Booking bookingFromDb = bookingService.getBookingById(addedBooking.getId(), bookerId);
        // Check results
        assertThat(bookingFromDb.getId(), equalTo(addedBooking.getId()));
        assertThat(bookingFromDb.getStart(), equalTo(newBooking.getStart()));
        assertThat(bookingFromDb.getEnd(), equalTo(newBooking.getEnd()));
        assertThat(bookingFromDb.getItem().getId(), equalTo(newBooking.getItem().getId()));
        assertThat(bookingFromDb.getBooker().getId(), equalTo(newBooking.getBooker().getId()));
        assertThat(bookingFromDb.getStatus(), equalTo(newBooking.getStatus()));

        // Get not existed booking
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(100L, bookerId));
    }

    @Test
    public void getAllBookingsByUserFilteredByStateTest() {
        // Create itemOwner and booker. Add them to DB
        User itemOwner = createUser(1);
        User booker = createUser(2);
        addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item. Add item to DB
        Item item = createItem(1, itemOwner);
        addItemToDb(item);
        // Create bookings. Add booking to DB
        Booking pastBooking = createBooking("2000-01-01 | 10:00:00", "2000-01-01 | 12:00:00", item, booker);
        Booking currentBooking = createBooking("2020-01-01 | 10:00:00", "2030-01-01 | 12:00:00", item, booker);
        Booking futureBooking = createBooking("2040-01-01 | 10:00:00", "2040-01-01 | 12:00:00", item, booker);
        Booking addedPastBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(pastBooking), bookerId);
        Booking addedCurrentBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(currentBooking), bookerId);
        Booking addedFutureBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(futureBooking), bookerId);

        // Get bookings with state==ALL
        List<Booking> allBookings = bookingService.getAllBookingsByUserFilteredByState("ALL", bookerId, 0, 10);
        assertThat(allBookings.size(), equalTo(3));

        // Get bookings with state==PAST
        List<Booking> pastBookings = bookingService.getAllBookingsByUserFilteredByState("PAST", bookerId, 0, 10);
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0).getId(), equalTo(addedPastBooking.getId()));

        // Get bookings with state==CURRENT
        List<Booking> currentBookings = bookingService.getAllBookingsByUserFilteredByState("CURRENT", bookerId, 0, 10);
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0).getId(), equalTo(addedCurrentBooking.getId()));

        // Get bookings with state==FUTURE
        List<Booking> futureBookings = bookingService.getAllBookingsByUserFilteredByState("FUTURE", bookerId, 0, 10);
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.get(0).getId(), equalTo(addedFutureBooking.getId()));

        // Get bookings with state==WAITING
        List<Booking> waitingBookings = bookingService.getAllBookingsByUserFilteredByState("WAITING", bookerId, 0, 10);
        assertThat(waitingBookings.size(), equalTo(3));

        // Get bookings with state==REJECTED
        List<Booking> rejectedBookings = bookingService.getAllBookingsByUserFilteredByState("REJECTED", bookerId, 0, 10);
        assertThat(rejectedBookings.size(), equalTo(0));
    }

    @Test
    public void getAllBookingsByItemsOwnerFilteredByStateTest() {
        // Create itemOwner and booker. Add them to DB
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Long itemOwnerId = addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item. Add item to DB
        Item item = createItem(1, itemOwner);
        addItemToDb(item);
        // Create bookings. Add booking to DB
        Booking pastBooking = createBooking("2000-01-01 | 10:00:00", "2000-01-01 | 12:00:00", item, booker);
        Booking currentBooking = createBooking("2020-01-01 | 10:00:00", "2030-01-01 | 12:00:00", item, booker);
        Booking futureBooking = createBooking("2040-01-01 | 10:00:00", "2040-01-01 | 12:00:00", item, booker);
        Booking addedPastBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(pastBooking), bookerId);
        Booking addedCurrentBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(currentBooking), bookerId);
        Booking addedFutureBooking = bookingService.addBooking(BookingMapper.toShortBookingDto(futureBooking), bookerId);

        // Get bookings with state==ALL
        List<Booking> allBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("ALL", itemOwnerId, 0, 10);
        assertThat(allBookings.size(), equalTo(3));

        // Get bookings with state==PAST
        List<Booking> pastBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("PAST", itemOwnerId, 0, 10);
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0).getId(), equalTo(addedPastBooking.getId()));

        // Get bookings with state==CURRENT
        List<Booking> currentBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("CURRENT", itemOwnerId, 0, 10);
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0).getId(), equalTo(addedCurrentBooking.getId()));

        // Get bookings with state==FUTURE
        List<Booking> futureBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("FUTURE", itemOwnerId, 0, 10);
        assertThat(futureBookings.size(), equalTo(1));
        assertThat(futureBookings.get(0).getId(), equalTo(addedFutureBooking.getId()));

        // Get bookings with state==WAITING
        List<Booking> waitingBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("WAITING", itemOwnerId, 0, 10);
        assertThat(waitingBookings.size(), equalTo(3));

        // Get bookings with state==REJECTED
        List<Booking> rejectedBookings = bookingService.getAllBookingsByItemsOwnerFilteredByState("REJECTED", itemOwnerId, 0, 10);
        assertThat(rejectedBookings.size(), equalTo(0));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private User createUser(int i) {
        return User.builder()
                .name("User" + i)
                .email("user@email.com" + i)
                .items(new ArrayList<>())
                .requests(new ArrayList<>())
                .build();
    }

    private Item createItem(int i, User owner) {
        return Item.builder()
                .name("Item" + i)
                .description("Desc" + i)
                .available(true)
                .owner(owner)
                .request(null)
                .build();
    }

    private Booking createBooking(String start, String end,
                                  Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.parse(start, formatter))
                .end(LocalDateTime.parse(end, formatter))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    private Long addUserToDb(User user) {
        em.persist(user);
        return user.getId();
    }

    private Long addItemToDb(Item item) {
        em.persist(item);
        return item.getId();
    }

    private Booking getBookingFromDb(Long id) {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = ?1", Booking.class);
        return query.setParameter(1, id).getSingleResult();
    }
}
