package ru.practicum.shareit.request_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {

    private final ItemRequestService requestService;
    private final EntityManager em;

    @Test
    public void addItemRequestTest() {
        // Create requestor. Add requestor to DB
        User requestor = createUser();
        Long requestorId = addUserToDb(requestor);
        // Create ItemRequestDto
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(createItemRequest(requestor, 1));

        // Add request to DB
        ItemRequest itemRequest = requestService.addItemRequest(requestorId, itemRequestDto);
        // Get request from DB
        ItemRequest itemRequestFromDb = getItemRequestFromDb(itemRequest.getId());
        // Check results
        assertThat(itemRequestFromDb.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestFromDb.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestFromDb.getCreated(), is(notNullValue()));

        // Add request to DB by not existed user
        assertThrows(NotFoundException.class, () -> requestService.addItemRequest(100L, itemRequestDto));
    }

    @Test
    public void getItemRequestsTest() {
        // Create requestor. Add requestor to DB
        User requestor = createUser();
        Long requestorId = addUserToDb(requestor);
        // Create ItemRequestDto
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(createItemRequest(requestor, 1));
        // Add request to DB
        ItemRequest itemRequest = requestService.addItemRequest(requestorId, itemRequestDto);

        // Get request from DB
        GetItemRequestDto itemRequestFromDb = requestService.getItemRequestsById(requestorId, itemRequest.getId());
        // Check results
        assertThat(itemRequestFromDb.getId(), equalTo(itemRequest.getId()));
        assertThat(itemRequestFromDb.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestFromDb.getCreated(), is(notNullValue()));
        assertThat(itemRequestFromDb.getItems(), is(empty()));

        // Get request by not existed user
        assertThrows(NotFoundException.class, () -> requestService.getItemRequestsById(100L, itemRequest.getId()));

        // Get not existed request
        assertThrows(NotFoundException.class, () -> requestService.getItemRequestsById(requestorId, 100L));
    }

    @Test
    public void getAllAnotherUsersItemRequestsTest() {
        // Create users. Add requestor to DB
        User user1 = createUser("user1@mail.com");
        User user2 = createUser("user2@mail.com");
        Long user1Id = addUserToDb(user1);
        Long user2Id = addUserToDb(user2);
        // Create ItemRequestDto
        ItemRequestDto itemRequestDto1 = ItemRequestMapper.toItemRequestDto(createItemRequest(user2, 1));
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(createItemRequest(user1, 2));
        ItemRequestDto itemRequestDto3 = ItemRequestMapper.toItemRequestDto(createItemRequest(user1, 3));
        // Add requests to DB
        ItemRequest addedItemRequest1 = requestService.addItemRequest(user2Id, itemRequestDto1);
        ItemRequest addedItemRequest2 = requestService.addItemRequest(user1Id, itemRequestDto2);
        ItemRequest addedItemRequest3 = requestService.addItemRequest(user1Id, itemRequestDto3);

        // Get other users requests by user1
        List<GetItemRequestDto> list1 = requestService.getAllAnotherUsersItemRequests(user1Id, 0, 10);
        List<GetItemRequestDto> list2 = requestService.getAllAnotherUsersItemRequests(user2Id, 0, 10);
        // Check results
        assertThat(list1.size(), equalTo(1));
        assertThat(list1.get(0).getId(), equalTo(addedItemRequest1.getId()));
        assertThat(list2.size(), equalTo(2));
        assertThat(list2.get(0).getId(), equalTo(addedItemRequest2.getId()));
        assertThat(list2.get(1).getId(), equalTo(addedItemRequest3.getId()));
    }

    private User createUser(String...strings) {
        String email = strings.length > 0 ? strings[0] : "user@email.com";
        return User.builder()
                .name("User")
                .email(email)
                .items(new ArrayList<>())
                .requests(new ArrayList<>())
                .build();
    }

    private ItemRequest createItemRequest(User requestor, int i) {
        return ItemRequest.builder()
                .description("description" + i)
                .requestor(requestor)
                .build();
    }

    private Long addUserToDb(User user) {
        em.persist(user);
        return user.getId();
    }

    private ItemRequest getItemRequestFromDb(Long requestId) {
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.id = ?1", ItemRequest.class);
        return query.setParameter(1, requestId).getSingleResult();
    }
}
