package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.CoffeeCup;
import ru.otus.hw.service.MakeCoffeeGateway;
import ru.otus.hw.models.GreenBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CoffeeFlowTest {

    @Autowired
    private MakeCoffeeGateway coffeeGateway;

    @Test
    void shouldMakeCoffeeForGoodBean() {
        GreenBean goodBean = new GreenBean("Colombia", 8);

        CoffeeCup cup = coffeeGateway.makeCoffee(goodBean);

        assertThat(cup).isNotNull();
    }

    @Test
    void shouldReturnNullForBadBean() {
        GreenBean badBean = new GreenBean("Ethiopia", 3);

        CoffeeCup cup = coffeeGateway.makeCoffee(badBean);

        assertThat(cup).isNull();
    }
}
