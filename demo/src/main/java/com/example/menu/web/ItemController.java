// src/main/java/com/example/menu/web/ItemController.java
package com.example.menu.web;

import com.example.menu.model.Item;
import com.example.menu.model.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/menu/items")
@CrossOrigin(origins = "https://dashboard.whatabyte.app")
public class ItemController {

    private ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Collection<Item> items(){
        List<Item> list = new ArrayList<>();
        this.itemRepository.findAll().forEach(list::add);
        return list;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> item(@PathVariable Long id){
        return this.itemRepository.findById(id)
                .map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:items')")
    public ResponseEntity<Item> updateItem(@Valid @RequestBody Item items, @PathVariable Long id){
        return this.itemRepository.findById(id)
                .map(item -> {
                    item.setName(items.getName());
                    item.setPrice(items.getPrice());
                    item.setDescription(items.getDescription());
                    item.setImage(items.getImage());
                    Item result = this.itemRepository.save(item);
                    return ResponseEntity.ok().body(result);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('create:items')")
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) throws URISyntaxException {
        Item result = this.itemRepository.save(item);
        return ResponseEntity.created(new URI("/api/menu/items/" + result.getId())).body(result);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete:items')")
    public ResponseEntity<?> deleteItem(@PathVariable Long id){
        this.itemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }


}