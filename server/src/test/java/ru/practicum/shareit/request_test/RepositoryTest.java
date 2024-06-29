package ru.practicum.shareit.request_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RepositoryTest {

    private final TestEntityManager em;
    private final ItemRequestRepository requestRepository;

    @Test
    public void findAllTest() {
        // Create users and requests
        User user1 = createUser(1);
        User user2 = createUser(2);
        User user3 = createUser(3);
        ItemRequest request1 = createItemRequest(user1, 1);
        ItemRequest request2 = createItemRequest(user1, 2);
        ItemRequest request3 = createItemRequest(user2, 3);
        ItemRequest request4 = createItemRequest(user3, 4);
        long userId1 = addUserToDb(user1);
        long userId2 = addUserToDb(user2);
        addUserToDb(user3);
        addRequestToDb(request1);
        addRequestToDb(request2);
        long requestId3 = addRequestToDb(request3);
        long requestId4 = addRequestToDb(request4);
        // Get requests by user with id=1 page=0 size=10
        List<ItemRequest> requestsList1 =
                requestRepository.findAllByRequestorIdNot(userId1, PageRequest.of(0, 10)).getContent();
        List<Long> requestsIdsList1 = requestsList1.stream().map(ItemRequest::getId).collect(Collectors.toList());
        assertThat(requestsList1.size(), equalTo(2));
        assertTrue(requestsIdsList1.contains(requestId3));
        assertTrue(requestsIdsList1.contains(requestId4));
        // Get requests by user with id=2 page=2 size=1
        List<ItemRequest> requestsList2 =
                requestRepository.findAllByRequestorIdNot(userId2, PageRequest.of(2, 1)).getContent();
        List<Long> requestsIdsList2 = requestsList2.stream().map(ItemRequest::getId).collect(Collectors.toList());
        assertThat(requestsList2.size(), equalTo(1));
        assertTrue(requestsIdsList2.contains(requestId4));
        // Get requests with size=0
        assertThrows(IllegalArgumentException.class,
                () -> requestRepository.findAllByRequestorIdNot(userId1, PageRequest.of(0, 0)).getContent());
    }

    private User createUser(int i) {
        return User.builder()
                .name("User" + i)
                .email("user@email.com" + i)
                .build();
    }

    private long addUserToDb(User userToDb) {
        em.persist(userToDb);
        return userToDb.getId();
    }

    private ItemRequest createItemRequest(User user, long i) {
        return ItemRequest.builder()
                .description("RequestDesc" + i)
                .created(LocalDateTime.parse("2000-01-01 | 10:00:00",
                         DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss")))
                .requestor(user)
                .build();
    }

    private long addRequestToDb(ItemRequest request) {
        em.persist(request);
        return request.getId();
    }
}
