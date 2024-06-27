package ru.practicum.shareit.request_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void addItemRequestTest() throws Exception {
        // Create request. Setup mock
        ItemRequestDto requestDto = getRequestDto("description");
        when(requestService.addItemRequest(anyLong(), any()))
                .thenReturn(ItemRequestMapper.toItemRequest(requestDto, User.builder().id(1L).build()));
        // Check endpoint
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.created", is("2000-01-01T12:00:00")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    public void addItemRequestWithoutUserIdParamTest() throws Exception {
        // Create request
        ItemRequestDto requestDto = getRequestDto("description");
        // Check endpoint
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void addItemRequestWithIncorrectDescriptionTest() throws Exception {
        // Create request
        ItemRequestDto requestDto = getRequestDto("");
        // Check endpoint
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getUserItemRequestsTest() throws Exception {
        // Create request. Setup mock
        when(requestService.getItemRequests(anyLong()))
                .thenReturn(List.of(GetItemRequestDto.builder().build()));
        // Check endpoint
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)));
    }

    @Test
    public void getUserItemRequestsWithIncorrectUserIdHeaderTest() throws Exception {
        // Check endpoint
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", -100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getItemRequestByIdTest() throws Exception {
        // Create request. Setup mock
        ItemRequestDto requestDto = getRequestDto("description");
        when(requestService.getItemRequestsById(anyLong(), anyLong()))
                .thenReturn(GetItemRequestDto.builder().id(1L).build());
        // Check endpoint
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class));
    }

    @Test
    public void getItemRequestByIdWithIncorrectRequestIdTest() throws Exception {
        // Create incorrect request id
        int incorrectId = -100;
        // Check endpoint
        mvc.perform(get("/requests/" + incorrectId)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getItemRequestByIdWithIncorrectUserIdParamTest() throws Exception {
        // Check endpoint
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", -100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllAnotherUsersItemRequestsTest() throws Exception {
        // Setup mock
        when(requestService.getAllAnotherUsersItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(GetItemRequestDto.builder().id(1L).build()));
        // Check endpoint
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)));
    }

    @Test
    public void getAllAnotherUsersItemRequests_WithIncorrectUserIdHeader_Test() throws Exception {
        // Check endpoint
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", -100L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllAnotherUsersItemRequests_WithIncorrectFromParam_Test() throws Exception {
        // Check endpoint
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-100")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    public void getAllAnotherUsersItemRequests_WithIncorrectSizeParam_Test() throws Exception {
        // Check endpoint
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "-100")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    private ItemRequestDto getRequestDto(String description) {
        return ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.parse("2000-01-01 12:00:00",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .description(description)
                .build();
    }
}
