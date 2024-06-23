package ru.practicum.shareit.user_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void addUserTest() throws Exception {
        // Create user. Setup mock
        UserDto userDto = getUser(1L);
        when(userService.addUser(any())).thenReturn(UserMapper.toUser(userDto));
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void addUserWithNoEmailTest() throws Exception {
        // Create user with no email
        UserDto userDto = getUser(1L, "Name1");
        userDto.setEmail(null);
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addUserWithIncorrectEmailTest() throws Exception {
        // Create user with incorrect email
        String incorrectEmail = "incorrect_email.com";
        UserDto userDto = getUser(1L, "Name1", incorrectEmail);
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addUserWithBlankNameTest() throws Exception {
        // Create user with blank name
        String blankName = "";
        UserDto userDto = getUser(1L, blankName);
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addUserWithDuplicateEmailTest() throws Exception {
        // Create user. Setup mock
        UserDto userDto = getUser(1L);
        when(userService.addUser(any())).thenThrow(new DataIntegrityViolationException("some message"));
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @Test
    public void addUserWithNoNameTest() throws Exception {
        // Create user with blank name
        UserDto userDto = getUser(1L);
        userDto.setName(null);
        // Check endpoint
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        // Create user. Setup mock
        UserDto userDto = getUser(1L);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUser(userDto));
        // Check endpoint
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void getUserByIncorrectIdTest() throws Exception {
        // Create incorrectId
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/users/" + incorrectId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllUsersTest() throws Exception {
        // Creat list of 3 users. Setup mock
        List<UserDto> userDtos = List.of(getUser(1L), getUser(2L), getUser(3L));
        when(userService.getAllUsers()).thenReturn(UserMapper.toUser(userDtos));
        // Check endpoint
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void getAllUsersWithEmptyListTest() throws Exception {
        // Create empty list of users. Setup mock
        List<UserDto> userDtos = List.of();
        when(userService.getAllUsers()).thenReturn(UserMapper.toUser(userDtos));
        // Check endpoint
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void updateUserTest() throws Exception {
        // Create user. Setup mock
        UserDto userDto = getUser(1L);
        when(userService.updateUser(any(), anyLong())).thenReturn(UserMapper.toUser(userDto));
        // Check endpoint
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void updateUserWithIncorrectEmailTest() throws Exception {
        // Create user with incorrect email
        String incorrectEmail = "incorrectEmail";
        UserDto userDto = getUser(1L, "Name1", incorrectEmail);
        // Check endpoint
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void updateUserWithBlankNameTest() throws Exception {
        // Create user with blank name
        String blankName = "";
        UserDto userDto = getUser(1L, blankName);
        // Check endpoint
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void updateUserWithIncorrectIdTest() throws Exception {
        // Create user with incorrect id
        long incorrectId = -100;
        UserDto userDto = getUser(incorrectId);
        // Check endpoint
        mvc.perform(patch("/users/" + incorrectId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void deleteUserTest() throws Exception {
        // Create user. Setup mock
        UserDto userDto = getUser(1L);
        when(userService.deleteUser(anyLong())).thenReturn(UserMapper.toUser(userDto));
        // Check endpoint
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    public void deleteUserWithIncorrectIdTest() throws Exception {
        // Create incorrectId
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(delete("/users/" + incorrectId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    private UserDto getUser(Long i, String...strings) {
        String name = strings.length > 0 ? strings[0] : "Name%d" + i;
        String email = strings.length > 1 ? strings[1] : "name%d@mail.ru" + i;
        return UserDto.builder()
                .id(i)
                .name(name)
                .email(email)
                .build();
    }
}
