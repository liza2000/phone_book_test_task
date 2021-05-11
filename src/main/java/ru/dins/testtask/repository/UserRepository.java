package ru.dins.testtask.repository;

import org.springframework.stereotype.Repository;
import ru.dins.testtask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   List<User> findUsersByName(String name);
   List<User> findUsersByNameContains(String name);
}
