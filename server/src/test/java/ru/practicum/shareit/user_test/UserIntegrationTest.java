package ru.practicum.shareit.user_test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTest {

    private final UserService userService;
    private final EntityManager em;

    @Test
    public void addUserTest() {
        // Create user
        User user = createUser(1);
        // Add user
        userService.addUser(user);
        // Get user
        User userFromDb = getUserFromDb();
        // Check users
        assertThat(userFromDb.getId(), notNullValue());
        assertThat(userFromDb.getName(), equalTo(user.getName()));
        assertThat(userFromDb.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void getUserByIdTest() {
        // Create user and add user to DB
        User user = createUser(1);
        long userId = addUserToDb(user);
        // Get user
        User userFromDb = userService.getUserById(userId);
        // Check users
        assertThat(userFromDb.getId(), equalTo(userId));
        assertThat(userFromDb.getName(), equalTo(user.getName()));
        assertThat(userFromDb.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void getAllUsersTest() {
        // Create users
        User user1 = createUser(1);
        User user2 = createUser(2);
        User user3 = createUser(3);
        addUserToDb(user1);
        addUserToDb(user2);
        addUserToDb(user3);
        // Get all users
        List<User> usersFromDb = userService.getAllUsers();
        List<String> usersNames = usersFromDb.stream().map(User::getName).collect(Collectors.toList());
        //Check list
        assertThat(usersFromDb.size(), equalTo(3));
        assertTrue(usersNames.contains(user1.getName()));
        assertTrue(usersNames.contains(user2.getName()));
        assertTrue(usersNames.contains(user3.getName()));
    }


    @Test
    public void updateUserTest() {
        // Create user
        User user = createUser(1);
        long userId = addUserToDb(user);
        // Get users list
        List<User> usersList = userService.getAllUsers();
        // Check list
        assertThat(usersList.size(), equalTo(1));
        // Update user
        user.setName("updatedUser");
        userService.updateUser(user, userId);
        // Get users list with updated user
        List<User> updatedUsersList = userService.getAllUsers();
        // Check users
        assertThat(updatedUsersList.size(), equalTo(1));
        assertThat(updatedUsersList.get(0).getName(), equalTo("updatedUser"));
    }

    @Test
    public void deleteUserTest() {
        // Create user
        User user = createUser(1);
        long userId = addUserToDb(user);
        // Get users list
        List<User> usersList = userService.getAllUsers();
        // Check list
        assertThat(usersList.size(), equalTo(1));
        // Delete user
        userService.deleteUser(userId);
        // Get users list
        List<User> newUsersList = userService.getAllUsers();
        // Check list
        assertThat(newUsersList.size(), equalTo(0));
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

    private User getUserFromDb() {
        TypedQuery<User> query = em.createQuery("select u from User as u where email = :email", User.class);
        return query.setParameter("email", "user@email.com" + 1).getSingleResult();
    }
}
