package ru.dins.testtask.controller;

import ru.dins.testtask.model.Item;
import ru.dins.testtask.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dins.testtask.repository.ItemRepository;
import ru.dins.testtask.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestParam String name) {
        User user = new User(0L, name);
        user = userRepository.save(user);
        return ResponseEntity.ok("User " + user.getName() + " successfully created");
    }

    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestParam Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        List<Item> items = itemRepository.findItemsByOwner(user);
        itemRepository.deleteAll(items);
        userRepository.delete(user);
        return ResponseEntity.ok("User " + user.getName() + " successfully deleted");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateName(@RequestParam Long id, @RequestParam String name) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        user.setName(name);
        user = userRepository.save(user);
        return ResponseEntity.ok("User with id " + user.getId() + " successfully updated to " + user.getName());
    }

    @GetMapping("/find_by_name")
    public ResponseEntity<List<User>> findByName(@RequestParam String name) {
        List<User> users = userRepository.findUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/find_by_part_of_name")
    public ResponseEntity<List<User>> findByPartOfName(@RequestParam String name) {
        List<User> users = userRepository.findUsersByNameContains(name);
        return ResponseEntity.ok(users);
    }


}
