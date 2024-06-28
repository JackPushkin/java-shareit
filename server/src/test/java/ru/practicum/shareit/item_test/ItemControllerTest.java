//package ru.practicum.shareit.item_test;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.item.controller.ItemController;
//import ru.practicum.shareit.item.dto.GetItemDto;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.mapper.ItemMapper;
//import ru.practicum.shareit.item.model.Comment;
//import ru.practicum.shareit.item.service.ItemService;
//import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.mapper.UserMapper;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = ItemController.class)
//public class ItemControllerTest {
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @MockBean
//    private ItemService itemService;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    public void addItemTest() throws Exception {
//        // Create item. Setup mock
//        ItemDto itemDto = getItem(1L, true);
//        when(itemService.addItem(any(), any(), any())).thenReturn(ItemMapper.toItem(itemDto));
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
//    }
//
//    @Test
//    public void addItemWithoutIdHeaderTest() throws Exception {
//        // Create item
//        ItemDto itemDto = getItem(1L, true);
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void addItemWithNotFoundUserTest() throws Exception {
//        // Create item. Setup mock
//        ItemDto itemDto = getItem(1L, true);
//        when(itemService.addItem(any(), any(), any())).thenThrow(new NotFoundException("someMessage"));
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(404));
//    }
//
//    @Test
//    public void addItemWithoutAvailableTest() throws Exception {
//        // Create item without available field
//        ItemDto itemDto = getItem(1L, null);
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void addItemWithEmptyNameTest() throws Exception {
//        // Create item with empty name
//        ItemDto itemDto = getItem(1L, null, "");
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void addItemWithEmptyDescriptionTest() throws Exception {
//        // Create item with empty name
//        ItemDto itemDto = getItem(1L, null, "Name", "");
//        // Check endpoint
//        mvc.perform(post("/items")
//                        .content(mapper.writeValueAsString(itemDto))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void getItemByIdTest() throws Exception {
//        // Create item. Setup mock
//        GetItemDto getItemDto = getGetItemDto(1L);
//        when(itemService.getItemById(any(), any())).thenReturn(getItemDto);
//        // Check endpoint
//        mvc.perform(get("/items/1")
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(getItemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(getItemDto.getName())))
//                .andExpect(jsonPath("$.description", is(getItemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(getItemDto.getAvailable()), Boolean.class))
//                .andExpect(jsonPath("$.comments", is(getItemDto.getComments())))
//                .andExpect(jsonPath("$.lastBooking", is(getItemDto.getLastBooking()), Booking.class))
//                .andExpect(jsonPath("$.nextBooking", is(getItemDto.getNextBooking()), Booking.class));
//    }
//
//    @Test
//    public void getItemByIdWithIncorrectIdTest() throws Exception {
//        // Create incorrect id
//        long incorrectId = -100L;
//        // Check endpoint
//        mvc.perform(get("/items/" + incorrectId)
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void getItemByIdWithoutIdHeaderTest() throws Exception {
//        // Check endpoint
//        mvc.perform(get("/items/1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void getUserItemsTest() throws Exception {
//        // Create list of items. Setup mock;
//        List<GetItemDto> items = List.of(getGetItemDto(1L),
//                getGetItemDto(2L), getGetItemDto(3L));
//        when(itemService.getUserItems(any(), any(), any())).thenReturn(items);
//        // Check endpoint. Param: from=0 size=10
//        mvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "0")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(3));
//    }
//
//    @Test
//    public void getUserItemsWithIncorrectSizeParamTest() throws Exception {
//        // Check endpoint. Param: from=0 size=0
//        mvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "0")
//                        .param("size", "0")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void getUserItemsWithIncorrectFromParamTest() throws Exception {
//        // Check endpoint. Param: from=-10 size=0
//        mvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "-10")
//                        .param("size", "0")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void getUserItemsWithoutFromAndSizeParamTest() throws Exception {
//        // Create list of items. Setup mock;
//        List<GetItemDto> items = List.of(getGetItemDto(1L),
//                getGetItemDto(2L), getGetItemDto(3L));
//        when(itemService.getUserItems(any(), any(), any())).thenReturn(items);
//        // Check endpoint
//        mvc.perform(get("/items")
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(3));
//    }
//
//    @Test
//    public void getUserItemsWithoutIdHeaderTest() throws Exception {
//        // Check endpoint
//        mvc.perform(get("/items")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void searchItemsTest() throws Exception {
//        // Create list of items. Setup mock;
//        List<ItemDto> items = List.of(getItem(1L, true),
//                getItem(2L, true), getItem(3L, true));
//        when(itemService.searchItems(any(), anyString(), any(), any())).thenReturn(ItemMapper.toItem(items));
//        // Check endpoint
//        mvc.perform(get("/items/search")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "0")
//                        .param("size", "10")
//                        .param("text", "Name")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(3));
//    }
//
//    @Test
//    public void searchItemsWithoutTextParamTest() throws Exception {
//        // Check endpoint
//        mvc.perform(get("/items/search")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "0")
//                        .param("size", "10")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void searchItemsWithIncorrectSizeParamTest() throws Exception {
//        // Check endpoint. Incorrect param size=0
//        mvc.perform(get("/items/search")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "0")
//                        .param("size", "0")
//                        .param("text", "Name")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void searchItemsWithIncorrectFromParamTest() throws Exception {
//        // Check endpoint. Incorrect param from=-10
//        mvc.perform(get("/items/search")
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("from", "-10")
//                        .param("size", "10")
//                        .param("text", "Name")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void searchItemsWithoutIdHeaderTest() throws Exception {
//        // Check endpoint
//        mvc.perform(get("/items/search")
//                        .param("from", "0")
//                        .param("size", "10")
//                        .param("text", "Name")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void deleteItemTest() throws Exception {
//        // Create item. Setup mock
//        ItemDto itemDto = getItem(1L, true);
//        when(itemService.deleteItem(any(), any())).thenReturn(ItemMapper.toItem(itemDto));
//        // Check endpoint
//        mvc.perform(delete("/items/1")
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
//                .andExpect(jsonPath("$.name", is(itemDto.getName())))
//                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
//                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
//    }
//
//    @Test
//    public void deleteItemWithIncorrectIdTest() throws Exception {
//        // Create incorrectId
//        long incorrectId = -100L;
//        // Check endpoint
//        mvc.perform(delete("/items/" + incorrectId)
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void deleteItemWithoutIdHeaderTest() throws Exception {
//        // Check endpoint
//        mvc.perform(delete("/items/1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(400));
//    }
//
//    @Test
//    public void addCommentToItemTest() throws Exception {
//        // Create comment. Setup mock
//        Comment comment = getComment();
//        when(itemService.addCommentToItem(any(), any(), any())).thenReturn(comment);
//        // Check endpoint
//        mvc.perform(post("/items/1/comment")
//                        .content(mapper.writeValueAsString(comment))
//                        .header("X-Sharer-User-Id", 1L)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
//                .andExpect(jsonPath("$.text", is(comment.getText())))
//                .andExpect(jsonPath("$.authorName", is(comment.getAuthor().getName())));
//    }
//
//    private Comment getComment() {
//        return Comment.builder()
//                .id(1L)
//                .text("Comment")
//                .creationDate(LocalDateTime.now())
//                .item(ItemMapper.toItem(getItem(1L, true)))
//                .author(UserMapper.toUser(getUser()))
//                .build();
//    }
//
//    private ItemDto getItem(Long i, Boolean available, String...strings) {
//        String name = strings.length > 0 ? strings[0] : "Name" + i;
//        String description = strings.length > 1 ? strings[1] : "Desc" + i;
//        return ItemDto.builder()
//                .id(i)
//                .name(name)
//                .description(description)
//                .available(available)
//                .build();
//    }
//
//    private GetItemDto getGetItemDto(Long i) {
//        return GetItemDto.builder()
//                .id(i)
//                .name("Name" + i)
//                .description("Desc" + i)
//                .available(true)
//                .build();
//    }
//
//    private UserDto getUser() {
//        return UserDto.builder()
//                .id(1L)
//                .name("Name" + (Long) 1L)
//                .email("name@mail.ru" + (Long) 1L)
//                .build();
//    }
//}
