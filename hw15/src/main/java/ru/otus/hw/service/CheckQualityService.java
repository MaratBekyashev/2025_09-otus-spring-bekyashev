package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.GreenBean;

@Service
@Slf4j
public class CheckQualityService {

    public boolean isGood(GreenBean bean) {
        log.info("Проверяем качество зерна: {}", bean);
        var result = bean.getQualityScore() >= 5;
        return result;
    }
}
