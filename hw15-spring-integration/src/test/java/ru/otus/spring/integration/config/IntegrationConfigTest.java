package ru.otus.spring.integration.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.otus.spring.integration.domain.Butterfly;
import ru.otus.spring.integration.domain.Caterpillar;
import ru.otus.spring.integration.services.InsectGateway;
import ru.otus.spring.integration.services.TransformationService;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
class IntegrationConfigTest {

    @Autowired
    private InsectGateway insectGateway;

    @Test
    void shouldFilterOutDeadCaterpillars() {
        Caterpillar dead = new Caterpillar(1, false, 15, 15);
        Caterpillar alive = new Caterpillar(2, true, 10, 10);
        Caterpillar aliveAnother = new Caterpillar(3, true, 10, 10);

        Collection<Butterfly> result = insectGateway.transform(List.of(dead, alive, aliveAnother));

        assertThat(result).hasSize(2);
    }

    @TestConfiguration
    @EnableIntegration
    @IntegrationComponentScan(basePackageClasses = {InsectGateway.class})
    @ComponentScan(basePackageClasses = {TransformationService.class, IntegrationConfig.class})
    static class Config {
        @Bean(name = PollerMetadata.DEFAULT_POLLER)
        public PollerSpec poller() {
            return Pollers.fixedRate(100);
        }
    }
}