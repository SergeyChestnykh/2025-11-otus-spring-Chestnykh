package ru.otus.spring.integration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import ru.otus.spring.integration.domain.Caterpillar;
import ru.otus.spring.integration.domain.AbstractInsect;
import ru.otus.spring.integration.services.TransformationService;

import java.util.List;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> caterpillarChannel() {
        return MessageChannels.queue(10);
    }

    @Bean
    public MessageChannelSpec<?, ?> butterflyChannel() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public IntegrationFlow insectFlow(TransformationService transformationService) {
        return IntegrationFlow.from(caterpillarChannel())
                .handle(List.class, (p, h) ->
                        ((List<Caterpillar>) p).stream()
                                .filter(AbstractInsect::isAlive)
                                .map(transformationService::toChrysalis)
                                .filter(AbstractInsect::isAlive)
                                .map(transformationService::toButterfly)
                                .filter(AbstractInsect::isAlive)
                                .toList()
                )
                .channel(butterflyChannel())
                .get();
    }
}
