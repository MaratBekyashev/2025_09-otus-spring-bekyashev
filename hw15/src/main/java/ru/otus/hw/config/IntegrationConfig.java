package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import ru.otus.hw.models.GreenBean;
import ru.otus.hw.service.BrewingService;
import ru.otus.hw.service.CheckQualityService;
import ru.otus.hw.service.GrindingService;
import ru.otus.hw.service.RoastingService;

@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public MessageChannel coffeeInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow coffeeFlow(
            RoastingService roastingService,
            GrindingService grindingService,
            BrewingService brewingService,
            CheckQualityService checkQualityService) {
        return flow -> flow
                .channel(coffeeInputChannel())
                .filter(
                        GreenBean.class,
                        checkQualityService::isGood,
                        spec -> spec.discardFlow(discard -> discard
                                .handle(msg -> log.info("Отсев - бракованное зёрно: {}", msg.getPayload()))
                        )
                )
                .handle(roastingService, "roast")
                .handle(grindingService, "grind")
                .handle(brewingService, "brew");
    }
}
