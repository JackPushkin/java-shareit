package ru.practicum.shareit.item_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final TestEntityManager em;
    private final ItemRepository itemRepository;

    @Test
    public void searchItemsTest() {
        // Add items to DB
        addItemsToDb();
        // Get items from DB
        List<Item> items1 = itemRepository.searchItems("name", PageRequest.of(0, 10)).getContent();
        List<Item> items2 = itemRepository.searchItems("1", PageRequest.of(0, 10)).getContent();
        List<Item> items3 = itemRepository.searchItems("desc", PageRequest.of(0, 10)).getContent();
        // Check return values
        assertThat(items1.size(), equalTo(3));
        assertThat(items2.size(), equalTo(1));
        assertThat(items2.get(0).getName(), equalTo("name1"));
        assertThat(items3.size(), equalTo(3));
    }

    @Test
    public void findItemsByBookingsIdsTest() {
        // Add items to DB
        addItemsToDb();
        // Add bookings to DB
        addBookingsToDb();
        // Get items from DB
        List<Item> items1 = itemRepository.findItemsByBookingsIds(List.of(1L, 2L, 3L));
        List<Item> items2 = itemRepository.findItemsByBookingsIds(List.of(2L));
        // Check return values
        assertThat(items1.size(), equalTo(3));
        assertThat(items2.size(), equalTo(1));
        assertThat(items2.get(0).getId(), equalTo(getBookingById().getItem().getId()));
    }

    private void addItemsToDb() {
        Query query = em.getEntityManager().createNativeQuery(
                "INSERT INTO items (name, description, available) " +
                   "VALUES ('name1', 'desc1', true), " +
                          "('name2', 'desc2', true), " +
                          "('name3', 'desc3', true)");
        query.executeUpdate();
    }

    private void addBookingsToDb() {
        Query query = em.getEntityManager().createNativeQuery(
                "INSERT INTO bookings (start_booking, end_booking, id_item) " +
                        "VALUES (CURRENT_TIMESTAMP, '2100-01-01', 1), " +
                               "(CURRENT_TIMESTAMP, '2100-01-01', 2), " +
                               "(CURRENT_TIMESTAMP, '2100-01-01', 3)");
        query.executeUpdate();
    }

    private Booking getBookingById() {
        TypedQuery<Booking> query = em.getEntityManager().createQuery(
                "SELECT b FROM Booking AS b " +
                   "WHERE b.id = ?1", Booking.class);
        query.setParameter(1, 2L);
        return query.getSingleResult();
    }
}
