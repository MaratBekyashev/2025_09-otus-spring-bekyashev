package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.GroundCoffee;
import ru.otus.hw.models.RoastedBean;

// Помол зерен
@Service
@Slf4j
public class GrindingService {

    public GroundCoffee grind(RoastedBean bean) {
        log.info("Мелем зерно {}", bean);
        return new GroundCoffee(bean.getOrigin(), "fine");
    }
}