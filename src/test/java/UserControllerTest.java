import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.dins.testtask.Application;
import ru.dins.testtask.controller.UserController;
import ru.dins.testtask.model.User;
import ru.dins.testtask.repository.UserRepository;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
class UserControllerTest {
    @Autowired
    UserController userController;
    @Autowired
    UserRepository userRepository;

    static class UserAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return new User(accessor.getLong(0), accessor.getString(1));
        }
    }


    @BeforeAll
    void createUsers() {
        userController.createUser("Ivan");
        userController.createUser("Petr");
    }

    @ParameterizedTest
    @CsvSource({"1,Ivan", "2,Petr"})
    void savingTest(@AggregateWith(UserAggregator.class) User user) {
        User actualUser = userController.getUser(user.getId()).getBody();
        Assertions.assertEquals(user, actualUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Iv", "et", "Ivan", "bb"})
    void findByPartOfNameTest(String name) {
        List<User> actualUsers = userController.findByPartOfName(name).getBody();
        boolean passed = true;
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            passed = actualUsers.contains(user) ? user.getName().contains(name) : !user.getName().contains(name);
            if (!passed) break;
        }
        Assertions.assertTrue(passed);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ivan", "Petr", "bb"})
    void findByNameTest(String name) {
        List<User> actualUsers = userController.findByName(name).getBody();
        boolean passed = true;
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            passed = actualUsers.contains(user) ? user.getName().equals(name) : !user.getName().equals(name);
            if (!passed) break;
        }
        Assertions.assertTrue(passed);
    }

    @Test
    void updateUserTest() {
        User user = new User(0L, "temp");
        user = userRepository.save(user);
        userController.updateName(user.getId(), "temp1");
        User actualUser = userRepository.findById(user.getId()).orElse(null);
        User expectedUser = new User(user.getId(), "temp1");
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    void deleteUserTest() {
        User user = new User(0L, "temp");
        user = userRepository.save(user);
        userController.deleteUser(user.getId());
        User actualUser = userRepository.findById(user.getId()).orElse(null);
        Assertions.assertNull(actualUser);
    }

}
