package ru.dins.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dins.testtask.model.Item;
import ru.dins.testtask.model.User;
import ru.dins.testtask.repository.ItemRepository;
import ru.dins.testtask.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/item")
public class ItemController {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;


    @PostMapping("/create")
    public ResponseEntity<String> createItem(@RequestParam String name, @RequestParam String phoneNumber, @RequestParam Long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new EntityNotFoundException("User with id " + ownerId + " not found"));
        if (!phoneNumber.matches("^\\d{11}$"))
            return new ResponseEntity<>("Phone number is not correct", HttpStatus.BAD_REQUEST);
        Item item = new Item(0L, name, phoneNumber, user);
        item = itemRepository.save(item);
        return ResponseEntity.ok("Item " + item.getName() + " - " + item.getPhoneNumber() + " successfully created");
    }

    @GetMapping("/get")
    public ResponseEntity<Item> getItem(@RequestParam Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item with id " + id + " not found"));
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestParam Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item with id " + id + " not found"));
        itemRepository.delete(item);
        return ResponseEntity.ok("Item " + item.getName() + " - " + item.getPhoneNumber() + " successfully deleted");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateItem(@RequestParam Long id, @RequestParam String name, @RequestParam String phoneNumber) {
        if (!phoneNumber.matches("^\\d{11}$"))
            return new ResponseEntity<>("Phone number is not correct", HttpStatus.BAD_REQUEST);
        Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item with id " + id + " not found"));
        item.setName(name);
        item.setPhoneNumber(phoneNumber);
        item = itemRepository.save(item);
        return ResponseEntity.ok("Item with id " + item.getId() + " successfully updated to " + item.getName() + " - " + item.getPhoneNumber());
    }

    @GetMapping("/items_of_user")
    public ResponseEntity<List<Item>> getItemsByOwner(@RequestParam Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        List<Item> items = itemRepository.findItemsByOwner(user);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/find_by_number")
    public ResponseEntity<List<Item>> findByPhoneNumber(@RequestParam String number) {
        List<Item> items = itemRepository.findItemsByPhoneNumber(number);
        return ResponseEntity.ok(items);
    }

}
