package com.example.menu;

import com.example.menu.model.Item;
import com.example.menu.model.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
class Initializer implements CommandLineRunner {


    private ItemRepository itemRepository;

    public Initializer(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List list = List.of(
                new Item(1L, "Burger", BigDecimal.valueOf(599L), "Tasty", "https://cdn.auth0.com/blog/whatabyte/burger-sm.png"),
                new Item(2L, "Pizza", BigDecimal.valueOf(299L), "Cheesy", "https://cdn.auth0.com/blog/whatabyte/pizza-sm.png"),
                new Item(3L, "Tea", BigDecimal.valueOf(199L), "Informative", "https://cdn.auth0.com/blog/whatabyte/tea-sm.png")
        );
        this.itemRepository.saveAll(list);
    }
}
