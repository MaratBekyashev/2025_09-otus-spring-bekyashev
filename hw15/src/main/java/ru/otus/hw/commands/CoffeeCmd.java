package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.MakeCoffeeGateway;
import ru.otus.hw.models.CoffeeCup;
import ru.otus.hw.models.GreenBean;

import java.util.Random;

@RequiredArgsConstructor
@ShellComponent
@Slf4j
public class CoffeeCmd {

    private final MakeCoffeeGateway coffeeGateway;

    @ShellMethod(value = "Make coffee cup", key = "go")
    public void makeCoffeeCup() {
        for (int i = 0; i < 5; i++) {
           log.info("******* "+ i +"th cup ******");
            var bean = new GreenBean("Ethiopia", getBeanQuality());
            CoffeeCup cup = coffeeGateway.makeCoffee(bean);
            delay();
        }
    }

    private Integer getBeanQuality () {
       Random random = new Random();
       int result = random.nextInt(11);
       return result;
    }
    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
