package ru.otus.spring.integration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.spring.integration.services.MetamorphosisService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final MetamorphosisService metamorphosisService;

    @Override
    public void run(String... args) {
        metamorphosisService.startGenerateCaterpillarsLoop();
    }
}
