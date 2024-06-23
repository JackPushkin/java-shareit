package ru.practicum.shareit.item_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTest {

    private final ItemService itemService;
    private final EntityManager em;

    @Test
    public void addItemWithoutRequestIdTest() {
        // Create user and item
        User user = createUser(1);
        Item createdItem = createItem(1, true, user, null);
        // Add user to DB
        Long userId = addUserToDb(user);

        // Add item with incorrect userId to DB. Must be exception (NotFoundException)
        Long incorrectId = -100L;
        assertThrows(NotFoundException.class, () -> itemService.addItem(createdItem, incorrectId, null));
        // Add item without requestId to DB
        Item item = itemService.addItem(createdItem, userId, null);
        // Get item from DB
        Item itemFromDb = getItemFromDb(item.getId());
        // Check results
        assertThat(itemFromDb.getId(), equalTo(item.getId()));
        assertThat(itemFromDb.getName(), equalTo(createdItem.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemFromDb.getAvailable(), equalTo(createdItem.getAvailable()));
        assertThat(itemFromDb.getOwner().getId(), equalTo(createdItem.getOwner().getId()));
        assertThat(itemFromDb.getRequest(), equalTo(null));
    }

    @Test
    public void addItemWithRequestIdTest() {
        // Create item owner
        User itemOwner = createUser(1);
        // Create requestor
        User requestor = createUser(2);
        // Add users to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        addUserToDb(requestor);
        // Create request
        ItemRequest request = createRequest(requestor);
        // Add request to DB
        Long requestId = addRequestToDb(request);
        // Create item
        Item createdItem = createItem(1, true, itemOwner, request);

        // Add item with incorrect requestId to DB. Must be exception (NotFoundException)
        assertThrows(NotFoundException.class, () -> itemService.addItem(createdItem, itemOwnerId, -100L));
        // Add item to DB
        Item item = itemService.addItem(createdItem, itemOwnerId, requestId);
        // Get item from DB
        Item itemFromDb = getItemFromDb(item.getId());
        // Check results
        assertThat(itemFromDb.getId(), equalTo(item.getId()));
        assertThat(itemFromDb.getName(), equalTo(createdItem.getName()));
        assertThat(itemFromDb.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemFromDb.getAvailable(), equalTo(createdItem.getAvailable()));
        assertThat(itemFromDb.getOwner().getId(), equalTo(createdItem.getOwner().getId()));
        assertThat(itemFromDb.getRequest(), equalTo(createdItem.getRequest()));
    }

    @Test
    public void updateItemTest() {
        // Create itemOwner
        User itemOwner = createUser(1);
        // Add itemOwner to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        // Create item
        Item createdItem = createItem(1, true, itemOwner, null);
        // Add item to DB
        Long createdItemId = addItemToDb(createdItem);
        // Create updated item
        Item updatedItem = createItem(5, false, itemOwner, null);

        // Update item
        itemService.updateItem(updatedItem, createdItemId, itemOwnerId);
        // Get updated item from DB
        Item updatedItemFromDb = getItemFromDb(createdItemId);
        // Check results
        assertThat(updatedItemFromDb.getId(), equalTo(createdItemId));
        assertThat(updatedItemFromDb.getName(), equalTo(updatedItem.getName()));
        assertThat(updatedItemFromDb.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(updatedItemFromDb.getAvailable(), equalTo(updatedItem.getAvailable()));
        assertThat(updatedItemFromDb.getOwner().getId(), equalTo(updatedItem.getOwner().getId()));
        assertThat(updatedItemFromDb.getRequest(), equalTo(updatedItem.getRequest()));

        // Update item with incorrect userId. Must be exception (NotFoundException)
        Long incorrectItemOwnerId = -100L;
        assertThrows(NotFoundException.class, () -> itemService.updateItem(updatedItem, createdItemId, incorrectItemOwnerId));

        // Update not existed item. Must be exception (NotFoundException)
        Long incorrectItemId = -100L;
        assertThrows(NotFoundException.class, () -> itemService.updateItem(updatedItem, incorrectItemId, itemOwnerId));
    }

    @Test
    public void getItemByIdTest() {
        // Create itemOwner and booker
        User itemOwner = createUser(1);
        User booker = createUser(1, "booker@email.com");
        // Add itemOwner and booker to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item
        Item createdItem = createItem(1, true, itemOwner, null);
        // Add item to DB
        Long createdItemId = addItemToDb(createdItem);
        // Create booking
        Booking booking1 = createBooking(
                getUserFromDb(bookerId), getItemFromDb(createdItemId), "2005-01-01 10:00:00");
        Booking booking2 = createBooking(
                getUserFromDb(bookerId), getItemFromDb(createdItemId), "2040-01-01 10:00:00");
        // Add bookings to DB
        Long booking1Id = addBookingToDb(booking1);
        Long booking2Id = addBookingToDb(booking2);

        // Get item by another user
        GetItemDto itemFromDb1 = itemService.getItemById(createdItemId, bookerId);
        // Check results
        assertThat(itemFromDb1.getId(), equalTo(createdItemId));
        assertThat(itemFromDb1.getName(), equalTo(createdItem.getName()));
        assertThat(itemFromDb1.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemFromDb1.getAvailable(), equalTo(createdItem.getAvailable()));
        assertThat(itemFromDb1.getLastBooking(), equalTo(null));
        assertThat(itemFromDb1.getNextBooking(), equalTo(null));

        // Get item by item owner
        GetItemDto itemFromDb2 = itemService.getItemById(createdItemId, itemOwnerId);
        // Check results
        assertThat(itemFromDb2.getId(), equalTo(createdItemId));
        assertThat(itemFromDb2.getName(), equalTo(createdItem.getName()));
        assertThat(itemFromDb2.getDescription(), equalTo(createdItem.getDescription()));
        assertThat(itemFromDb2.getAvailable(), equalTo(createdItem.getAvailable()));
        assertThat(itemFromDb2.getLastBooking().getId(), equalTo(booking1Id));
        assertThat(itemFromDb2.getNextBooking().getId(), equalTo(booking2Id));
    }

    @Test
    public void getUserItemsTest() {
        // Create itemOwner
        User itemOwner = createUser(1);
        // Add itemOwner to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        // Create items
        Item createdItem1 = createItem(1, true, itemOwner, null);
        Item createdItem2 = createItem(2, true, itemOwner, null);
        Item createdItem3 = createItem(3, true, itemOwner, null);
        // Add items to DB
        Long createdItemId1 = addItemToDb(createdItem1);
        Long createdItemId2 = addItemToDb(createdItem2);
        Long createdItemId3 = addItemToDb(createdItem3);

        // Get user items from DB
        List<GetItemDto> itemsFromDb = itemService.getUserItems(itemOwnerId, 0, 10);
        Set<Long> itemsIds = itemsFromDb.stream().map(GetItemDto::getId).collect(Collectors.toSet());
        // Check results
        assertThat(itemsFromDb.size(), equalTo(3));
        assertTrue(itemsIds.contains(createdItemId1));
        assertTrue(itemsIds.contains(createdItemId2));
        assertTrue(itemsIds.contains(createdItemId3));

        // Get user items with incorrect ownerId from DB
        Long incorrectOwnerId = -100L;
        assertThrows(NotFoundException.class, () -> itemService.getUserItems(incorrectOwnerId, 0, 10));
    }

    @Test
    public void searchItemsTest() {
        // Create itemOwner
        User itemOwner = createUser(1);
        // Add itemOwner to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        // Create items
        Item createdItem1 = createItem(1, true, itemOwner, null);
        Item createdItem2 = createItem(2, true, itemOwner, null);
        Item createdItem3 = createItem(3, true, itemOwner, null);
        // Add items to DB
        Long createdItemId1 = addItemToDb(createdItem1);
        Long createdItemId2 = addItemToDb(createdItem2);
        Long createdItemId3 = addItemToDb(createdItem3);

        // Search items with empty text
        List<Item> items1 = itemService.searchItems(itemOwnerId, "", 0, 10);
        // Check results
        assertTrue(items1.isEmpty());

        // Search items with unknown text
        List<Item> items2 = itemService.searchItems(itemOwnerId, "XYZ", 0, 10);
        // Check results
        assertTrue(items2.isEmpty());

        // Search items with not existed user
        assertThrows(NotFoundException.class,
                () -> itemService.searchItems(-100L, "Item", 0, 10));

        // Search items with text == "Item"
        List<Item> items3 = itemService.searchItems(itemOwnerId, "Item", 0, 10);
        Set<Long> item3Ids = items3.stream().map(Item::getId).collect(Collectors.toSet());
        // Check results
        assertThat(items3.size(), equalTo(3));
        assertTrue(item3Ids.contains(createdItemId1));
        assertTrue(item3Ids.contains(createdItemId2));
        assertTrue(item3Ids.contains(createdItemId3));

        // Search items with text == "1"
        List<Item> items4 = itemService.searchItems(itemOwnerId, "1", 0, 10);
        // Check results
        assertThat(items4.size(), equalTo(1));
        assertThat(items4.get(0).getId(), equalTo(createdItemId1));

        // Search items with size == null and text == "Item"
        List<Item> items5 = itemService.searchItems(itemOwnerId, "Item", 0, null);
        // Check results
        assertThat(items5.size(), equalTo(3));
    }

    @Test
    public void deleteItemTest() {
        // Create itemOwner
        User itemOwner = createUser(1);
        // Add itemOwner to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        // Create item
        Item createdItem = createItem(1, true, itemOwner, null);
        // Add item to DB
        Long createdItemId1 = addItemToDb(createdItem);
        // Get items from DB
        assertThat(getItemsFromDb().size(), equalTo(1));

        // Delete item
        itemService.deleteItem(createdItemId1, itemOwnerId);
        // Check results
        assertTrue(getItemsFromDb().isEmpty());

        // Create user
        User user = createUser(2);
        // Add itemOwner to DB
        Long userId = addUserToDb(user);
        // Add item to DB
        Long createdItemId2 = addItemToDb(createItem(2, true, itemOwner, null));
        // Delete item by not owner. Must throw exception (NotFoundException)
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(createdItemId2, userId));

        // Delete item with incorrect id. Must throw exception (NotFoundException)
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(-100L, itemOwnerId));
    }

    @Test
    public void addCommentToItemTest() {
        // Create itemOwner and booker
        User itemOwner = createUser(1);
        User booker = createUser(2);
        // Add itemOwner and booker to DB
        Long itemOwnerId = addUserToDb(itemOwner);
        Long bookerId = addUserToDb(booker);
        // Create item
        Item item = createItem(1, true, itemOwner, null);
        // Add item to DB
        Long createdItemId = addItemToDb(item);
        // Create booking
        Booking booking = createBooking(
                getUserFromDb(bookerId), getItemFromDb(createdItemId), "2005-01-01 10:00:00");
        // Add booking to DB
        addBookingToDb(booking);
        // Create comment
        Comment comment = createComment(booker, item);

        // Add comment to item by booker after the end of the booking
        itemService.addCommentToItem(createdItemId, bookerId, comment);
        // Get item with comment
        GetItemDto itemFromDb = itemService.getItemById(createdItemId, itemOwnerId);
        // Check results
        CommentDto itemComment = itemFromDb.getComments().get(0);
        assertThat(itemFromDb.getComments().size(), equalTo(1));
        assertThat(itemComment.getText(), equalTo(comment.getText()));
        assertThat(itemComment.getAuthorName(), equalTo(comment.getAuthor().getName()));

        // Add comment to item not by booker
        assertThrows(NotAvailableException.class,
                () -> itemService.addCommentToItem(createdItemId, itemOwnerId, comment));

        // Add comment to item by booker before the end of the booking
        booking.setEnd(LocalDateTime.parse("2050-01-01 10:00:00",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        em.persist(booking);
        assertThrows(NotAvailableException.class,
                () -> itemService.addCommentToItem(createdItemId, itemOwnerId, comment));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Booking createBooking(User booker, Item item, String dateTime) {
        return Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .end(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .plus(Duration.ofDays(10)))
                .status(BookingStatus.APPROVED)
                .build();
    }

    private ItemRequest createRequest(User user) {
        return ItemRequest.builder()
                .description("Request")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
    }

    private Item createItem(int i, boolean available, User user, ItemRequest request) {
        return Item.builder()
                .name("Item" + i)
                .description("Desc" + i)
                .available(available)
                .owner(user)
                .request(request)
                .build();
    }

    private User createUser(int i, String...strings) {
        String email = strings.length > 0 ? strings[0] : "user@email.com";
        return User.builder()
                .name("User" + i)
                .email(email + i)
                .items(new ArrayList<>())
                .requests(new ArrayList<>())
                .build();
    }

    private Comment createComment(User author, Item item) {
        return Comment.builder()
                .text("Comment")
                .author(author)
                .item(item)
                .creationDate(LocalDateTime.now())
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

    private Long addRequestToDb(ItemRequest request) {
        em.persist(request);
        return request.getId();
    }

    private Long addBookingToDb(Booking booking) {
        em.persist(booking);
        return booking.getId();
    }

    private Item getItemFromDb(Long id) {
        TypedQuery<Item> query = em.createQuery(
                "SELECT i FROM Item AS i " +
                   "WHERE i.id = ?1", Item.class);
        return query.setParameter(1, id).getSingleResult();
    }

    private User getUserFromDb(Long id) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User AS u " +
                   "WHERE u.id = ?1", User.class);
        return query.setParameter(1, id).getSingleResult();
    }

    private List<Item> getItemsFromDb() {
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i", Item.class);
        return query.getResultList();
    }
}
