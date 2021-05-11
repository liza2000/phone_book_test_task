import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
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
import ru.dins.testtask.model.Item;
import ru.dins.testtask.model.User;
import ru.dins.testtask.repository.ItemRepository;
import ru.dins.testtask.repository.UserRepository;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = Application.class)
public class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    static class ItemAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {

            return new Item(accessor.getLong(0), accessor.getString(1),accessor.getString(2),new User(accessor.getLong(3),accessor.getString(4)));
        }
    }
    @BeforeAll
    void createUsers() {
        User ivan = new User(0L, "Ivan");
       ivan= userRepository.save(ivan);
        User petr = new User(0L,"Petr");
        petr =userRepository.save(petr);

        Item item = new Item(0L,"IvanItem1","88005553535", ivan);
        itemRepository.save(item);
        Item item2 = new Item(0L,"IvanItem2","88888888888",ivan);
        itemRepository.save(item2);
        Item item3 = new Item(0L,"PeterItem1","89601234567",petr);
        itemRepository.save(item3);
    }

    @ParameterizedTest
    @CsvSource({"1,IvanItem1,88005553535,1,Ivan","2,IvanItem2,88888888888,1,Ivan","3,PeterItem1,89601234567,2,Petr"})
    void savingTest(@AggregateWith(ItemRepositoryTest.ItemAggregator.class) Item item) {
        Item actualItem = itemRepository.findById(item.getId()).orElse(null);
        Assertions.assertEquals(item, actualItem);
    }

    @ParameterizedTest
    @ValueSource(longs = {1,2})
    void getByOwnerTest(Long id){

    }



    @ParameterizedTest
    @ValueSource(strings = {"88005553535", "88888888888", "89601234567","81111111111"})
    void findByPartOfName(String number) {
        List<Item> actualItems = itemRepository.findItemsByPhoneNumber(number);
        boolean passed = true;
        List<Item> allItems = itemRepository.findAll();
        for (Item item : allItems) {
            passed = actualItems.contains(item)?item.getPhoneNumber().equals(number):!item.getPhoneNumber().equals(number);
            if (!passed) break;
        }
        Assertions.assertTrue(passed);
    }


}
