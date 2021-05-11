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
import ru.dins.testtask.controller.ItemController;
import ru.dins.testtask.model.Item;
import ru.dins.testtask.model.User;
import ru.dins.testtask.repository.ItemRepository;
import ru.dins.testtask.repository.UserRepository;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
 class ItemControllerTest {

    @Autowired
    ItemController itemController;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    static class ItemAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {

            return new Item(accessor.getLong(0), accessor.getString(1), accessor.getString(2), new User(accessor.getLong(3), accessor.getString(4)));
        }
    }

    @BeforeAll
    void createItems() {
        User ivan = new User(0L, "Ivan");
        ivan = userRepository.save(ivan);
        User petr = new User(0L, "Petr");
        petr = userRepository.save(petr);
        itemController.createItem("IvanItem1", "88005553535", 1L);
        itemController.createItem("IvanItem2", "88888888888", 1L);
        itemController.createItem("PeterItem1", "89601234567", 2L);
    }

    @ParameterizedTest
    @CsvSource({"1,IvanItem1,88005553535,1,Ivan", "2,IvanItem2,88888888888,1,Ivan", "3,PeterItem1,89601234567,2,Petr"})
    void savingTest(@AggregateWith(ItemControllerTest.ItemAggregator.class) Item item) {
        Item actualItem = itemController.getItem(item.getId()).getBody();
        Assertions.assertEquals(item, actualItem);
    }

    @Test
    void updateItemTest() {
        User user = new User(0L, "tempUser");
        user = userRepository.save(user);
        Item item = new Item(0L, "temp", "11111111111", user);
        item = itemRepository.save(item);
        itemController.updateItem(item.getId(), "temp1", "11112222333");
        Item actualItem = itemRepository.findById(item.getId()).orElse(null);
        Item expectedItem = new Item(item.getId(), "temp1", "11112222333", user);
        Assertions.assertEquals(expectedItem, actualItem);
    }

    @Test
    void deleteItemTest(){
        User user = new User(0L, "tempUser");
        user = userRepository.save(user);
        Item item = new Item(0L, "temp", "11111111111", user);
        item = itemRepository.save(item);
        itemController.deleteItem(item.getId());
        Item actualItem = itemRepository.findById(item.getId()).orElse(null);
        Assertions.assertNull(actualItem);

    }

    @ParameterizedTest
    @CsvSource({"1,Ivan", "2,Petr"})
    void getByOwnerTest(@AggregateWith(UserControllerTest.UserAggregator.class) User user) {
        List<Item> actualItems = itemController.getItemsByOwner(user.getId()).getBody();
        List<Item> expectedItems = itemRepository.findItemsByOwner(user);
        Assertions.assertEquals(expectedItems, actualItems);
    }


    @ParameterizedTest
    @ValueSource(strings = {"88005553535", "88888888888", "89601234567", "81111111111"})
    void findByPhoneNumberTest(String number) {
        List<Item> actualItems = itemController.findByPhoneNumber(number).getBody();
        boolean passed = true;
        List<Item> allItems = itemRepository.findAll();
        for (Item item : allItems) {
            passed = actualItems.contains(item) ? item.getPhoneNumber().equals(number) : !item.getPhoneNumber().equals(number);
            if (!passed) break;
        }
        Assertions.assertTrue(passed);
    }


}
