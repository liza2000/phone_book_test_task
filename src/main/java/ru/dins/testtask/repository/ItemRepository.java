package ru.dins.testtask.repository;

import org.springframework.stereotype.Repository;
import ru.dins.testtask.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.dins.testtask.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByPhoneNumber(String phoneNumber);
    List<Item> findItemsByOwner(User user);
}
