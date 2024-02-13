package com.example.menu.web;

import com.example.menu.model.Item;
import com.example.menu.model.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetItems() throws Exception {
        given(itemRepository.findAll()).willReturn(List.of(new Item(1L, "Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png"), new Item(2L, "Pizza", BigDecimal.valueOf(299L), "Cheesy", "https://cdn.auth0.com/blog/whatabyte/pizza-sm.png"), new Item(3L, "Tea", BigDecimal.valueOf(199L), "Informative", "https://cdn.auth0.com/blog/whatabyte/tea-sm.png")));
        mockMvc.perform(get("/api/menu/items").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

    }

    @Test
    void testGetItem() throws Exception {
        given(itemRepository.findById(1L)).willReturn(java.util.Optional.of(new Item(1L, "Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png")));
        mockMvc.perform(get("/api/menu/items/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string("{\"id\":1,\"name\":\"Burger\",\"description\":\"Tasty\",\"price\":599,\"image\":\"https://cdn.auth0.com/blog/whatabyte/burger-sm.png\"}"));
    }

    @Test
    void testCreateItem() throws Exception {
        given(itemRepository.save(any(Item.class))).willReturn(new Item(1L, "Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png"));
        // @formatter:off
        mockMvc.perform(post("/api/menu/items")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Burger\",\"price\":599,\"description\":\"Tasty\",\"image\":\"https://cdn.auth0.com/blog/whatabyte/burger-sm.png\"}"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("{\"id\":1,\"name\":\"Burger\",\"description\":\"Tasty\",\"price\":599,\"image\":\"https://cdn.auth0.com/blog/whatabyte/burger-sm.png\"}"));
        // @formatter:on
    }

    @Test
    void testUpdateItem() throws Exception {
        given(itemRepository.findById(1L)).willReturn(java.util.Optional.of(new Item(1L, "Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png")));
        given(itemRepository.save(any(Item.class))).willReturn(new Item(1L, "Tasty Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png"));
        // @formatter:off
        mockMvc.perform(put("/api/menu/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tasty Burger\",\"price\":599,\"description\":\"Tasty\",\"image\":\"https://cdn.auth0.com/blog/whatabyte/burger-sm.png\"}"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("{\"id\":1,\"name\":\"Tasty Burger\",\"description\":\"Tasty\",\"price\":599,\"image\":\"https://cdn.auth0.com/blog/whatabyte/burger-sm.png\"}"));
        // @formatter:on
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/menu/items/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
