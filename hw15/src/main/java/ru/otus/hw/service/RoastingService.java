package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.GreenBean;
import ru.otus.hw.models.RoastedBean;

@Service
@Slf4j
public class RoastingService {

    public RoastedBean roast(GreenBean bean) {
        log.info("Жарим зерно {}", bean);
        return new RoastedBean(bean.getOrigin(), "medium");
    }
}