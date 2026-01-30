package ru.otus.hw.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.models.CoffeeCup;
import ru.otus.hw.models.GreenBean;

@MessagingGateway(defaultRequestChannel = "coffeeInputChannel", defaultReplyTimeout = "1000")
public interface MakeCoffeeGateway {

    @Gateway
    CoffeeCup makeCoffee(GreenBean bean);
}
