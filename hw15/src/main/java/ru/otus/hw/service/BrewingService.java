package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.CoffeeCup;
import ru.otus.hw.models.GroundCoffee;

// Заварить кофе
@Service
@Slf4j
public class BrewingService {

    public CoffeeCup brew(GroundCoffee coffee) {
        log.info("Извольте чашечку ароматного кофе из молотого зерна ({}): ", coffee);
        return new CoffeeCup(coffee.getOrigin());
    }
}